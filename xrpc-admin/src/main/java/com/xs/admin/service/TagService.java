package com.xs.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xs.admin.common.BizCode;
import com.xs.admin.common.DefineException;
import com.xs.admin.dto.TagDTO;
import com.xs.admin.repository.GenericRepository;
import com.xs.datamodel.customer.CustomerRequestTag;
import com.xs.datamodel.customer.CustomerResponseTag;
import com.xs.datamodel.provider.ProviderRequestTag;
import com.xs.datamodel.provider.ProviderResponseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.*;

@Service
public class TagService {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(TagService.class);


    @Autowired
    private GenericRepository repository;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Map<String, Class<?>> OWNER_CLASS = new HashMap<>();
    private static final Map<String, String> OWNER_FIELD = new HashMap<>();

    static {
        OWNER_CLASS.put("CUSTOMER_REQUEST", CustomerRequestTag.class);
        OWNER_CLASS.put("CUSTOMER_RESPONSE", CustomerResponseTag.class);
        OWNER_CLASS.put("PROVIDER_REQUEST", ProviderRequestTag.class);
        OWNER_CLASS.put("PROVIDER_RESPONSE", ProviderResponseTag.class);
        OWNER_FIELD.put("CUSTOMER_REQUEST", "customerTranNo");
        OWNER_FIELD.put("CUSTOMER_RESPONSE", "customerTranNo");
        OWNER_FIELD.put("PROVIDER_REQUEST", "providerTranNo");
        OWNER_FIELD.put("PROVIDER_RESPONSE", "providerTranNo");
    }

    private Class<?> clazz(String ownerType) {
        Class<?> c = OWNER_CLASS.get(ownerType);
        if (c == null) throw new DefineException(BizCode.PARAM_ERROR, "未知 Tag 类型：" + ownerType);
        return c;
    }

    public List<TagDTO> list(String ownerType, String ownerKey) {
        List<?> rows = repository.findBy(clazz(ownerType), OWNER_FIELD.get(ownerType), ownerKey);
        List<TagDTO> out = new ArrayList<>();
        rows.forEach(r -> out.add(mapper.convertValue(r, TagDTO.class)));
        out.sort(Comparator.comparing(TagDTO::getParentTag, Comparator.nullsLast(String::compareTo))
                .thenComparing(TagDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        return out;
    }

    @Transactional
    public int batch(String ownerType, String ownerKey, List<Map<String, Object>> tags) {
        Class<?> c = clazz(ownerType);
        String field = OWNER_FIELD.get(ownerType);
        repository.findBy(c, field, ownerKey).forEach(repository::deleteRaw);
        repository.flush();
        int count = 0;
        for (Map<String, Object> t : tags) {
            Map<String, Object> data = new HashMap<>(t);
            data.put(field, ownerKey);
            Object entity = mapper.convertValue(data, c);
            repository.persist(entity);
            count++;
        }
        return count;
    }

    @Transactional
    public int copy(String fromOwnerType, String fromOwnerKey, String toOwnerType, String toOwnerKey) {
        List<TagDTO> source = list(fromOwnerType, fromOwnerKey);
        List<Map<String, Object>> data = new ArrayList<>();
        source.forEach(t -> {
            Map<String, Object> m = mapper.convertValue(t, Map.class);
            m.remove("id");
            if (m.get("parentTag") != null) {
                m.put("parentTag", String.valueOf(m.get("parentTag")).replace(fromOwnerKey, toOwnerKey));
            }
            data.add(m);
        });
        return batch(toOwnerType, toOwnerKey, data);
    }

    public Map<String, Object> validate(List<TagDTO> tags, String ownerKey, String protocol) {
        List<Map<String, Object>> errors = new ArrayList<>();
        Set<String> names = new HashSet<>();
        Map<String, Set<Integer>> sorts = new HashMap<>();
        for (TagDTO t : tags) {
            if (t.getTagName() == null || t.getTagName().trim().isEmpty()) addError(errors, t.getId(), "tagName", "标签名为空");
            String key = t.getParentTag() + "|" + t.getTagName();
            if (!names.add(key)) addError(errors, t.getId(), "tagName", "同级标签名重复");
            String parent = t.getParentTag() == null ? "" : t.getParentTag();
            if (!parent.equals(ownerKey) && !parent.startsWith(ownerKey + ".")) {
                addError(errors, t.getId(), "parentTag", "父路径根节点不是交易码");
            }
            if (parent.split("\\.").length > 20) addError(errors, t.getId(), "parentTag", "层级超过 20");
            String type = t.getTagType();
            if (type != null && !Arrays.asList("String", "Object", "List", "SizeList", "NoSizeList").contains(type)) {
                addError(errors, t.getId(), "tagType", "类型非法");
            }
            if ("dc".equalsIgnoreCase(protocol) && ("String".equals(type) || "List".equals(type))
                    && (t.getTaglength() == null || t.getTaglength() <= 0)) {
                addError(errors, t.getId(), "taglength", "定长报文标量字段必须配置长度");
            }
            Set<Integer> set = sorts.computeIfAbsent(parent, k -> new HashSet<>());
            if (t.getSort() != null && !set.add(t.getSort())) addError(errors, t.getId(), "sort", "同级排序号重复");
        }
        Map<String, Object> res = new HashMap<>();
        res.put("passed", errors.isEmpty());
        res.put("errors", errors);
        res.put("warnings", Collections.emptyList());
        res.put("preview", Collections.emptyList());
        return res;
    }

    public List<TagDTO> infer(String sample) {
        List<TagDTO> out = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(sample);
            walk(root, "", "", out);
        } catch (Exception ignored) { }
        return out;
    }

    private void walk(JsonNode node, String parentTag, String path, List<TagDTO> out) {
        if (node == null || node.isNull()) return;
        String name = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        if (node.isArray()) {
            TagDTO t = base(name, parentTag, path, "SizeList", out.size() + 1);
            t.setTagflag("ListSizeCount");
            t.setTagValue(String.valueOf(node.size()));
            out.add(t);
            if (node.size() > 0) walk(node.get(0), parentTag.isEmpty() ? name : parentTag + "." + name, path + "[0]", out);
            return;
        }
        if (node.isObject()) {
            TagDTO t = base(name, parentTag, path, "Object", out.size() + 1);
            out.add(t);
            String nextParent = parentTag.isEmpty() ? name : parentTag + "." + name;
            node.fieldNames().forEachRemaining(f -> walk(node.get(f), nextParent, path.isEmpty() ? f : path + "." + f, out));
            return;
        }
        TagDTO t = base(name, parentTag, path, "String", out.size() + 1);
        t.setTagValue(node.asText());
        out.add(t);
    }

    private TagDTO base(String name, String parentTag, String path, String type, int sort) {
        TagDTO t = new TagDTO();
        t.setTagName(name); t.setParentTag(parentTag); t.setTagMapping(path);
        t.setTagType(type); t.setSort(sort);
        t.setId(sort);
        return t;
    }

    private void addError(List<Map<String, Object>> errors, Integer id, String field, String message) {
        Map<String, Object> e = new HashMap<>();
        e.put("tagId", id); e.put("field", field); e.put("message", message);
        errors.add(e);
    }
}
