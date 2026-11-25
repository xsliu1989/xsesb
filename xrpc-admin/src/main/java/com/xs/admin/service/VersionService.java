package com.xs.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xs.admin.common.BizCode;
import com.xs.admin.common.DefineException;
import com.xs.admin.common.PageResult;
import com.xs.admin.dto.VersionDraftDTO;
import com.xs.admin.entity.ConfigVersion;
import com.xs.admin.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VersionService {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(VersionService.class);


    @Autowired
    private GenericRepository repository;
    @Autowired
    private TagService tagService;
    @Autowired
    private MetaService metaService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${esb.effect-mode:RESTART_REQUIRED}")
    private String effectMode;

    private static final String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public PageResult<Object> page(int page, int size, Map<String, Object> filters) {
        long total = repository.count(ConfigVersion.class, filters);
        return PageResult.of(page, size, total,
                new ArrayList<>(repository.list(ConfigVersion.class, page, size, filters)));
    }

    public ConfigVersion draft(VersionDraftDTO dto, String operator) {
        if (dto.getTargetType() == null || dto.getTargetKey() == null) {
            throw new DefineException(BizCode.PARAM_ERROR, "对象类型与对象键必填");
        }
        ConfigVersion v = new ConfigVersion();
        v.setEnv(dto.getEnv() == null ? "dev" : dto.getEnv());
        v.setTargetType(dto.getTargetType());
        v.setTargetKey(dto.getTargetKey());
        v.setVersionNo(nextVersionNo(dto.getTargetKey()));
        v.setStatus("DRAFT");
        try { v.setContentJson(mapper.writeValueAsString(dto.getContent() == null ? Collections.emptyList() : dto.getContent())); }
        catch (Exception e) { throw new DefineException(BizCode.SYSTEM_ERROR, "内容序列化失败"); }
        v.setDraftBy(operator);
        v.setRemark(dto.getRemark());
        v.setCreateTime(now());
        repository.persist(v);
        return v;
    }

    private int nextVersionNo(String targetKey) {
        long c = repository.count(ConfigVersion.class, Collections.<String, Object>singletonMap("targetKey", targetKey));
        return (int) c + 1;
    }

    @Transactional
    public ConfigVersion approve(Long id) {
        ConfigVersion v = require(id);
        if (!"DRAFT".equals(v.getStatus())) throw new DefineException(BizCode.VERSION_STATE_ERROR, "仅草稿可审批");
        v.setStatus("APPROVED");
        return repository.save(v);
    }

    @Transactional
    public Map<String, Object> publish(Long id, String operator) {
        ConfigVersion v = require(id);
        if (!"APPROVED".equals(v.getStatus())) throw new DefineException(BizCode.VERSION_STATE_ERROR, "仅已审批版本可发布");
        List<Map<String, Object>> content = readContent(v);
        apply(v.getTargetType(), v.getTargetKey(), content);
        v.setStatus("PUBLISHED");
        v.setPublishTime(now());
        v.setApproveBy(operator);
        repository.save(v);
        Map<String, Object> res = new HashMap<>();
        res.put("version", v);
        res.put("effectMode", effectMode);
        return res;
    }

    @Transactional
    public ConfigVersion rollback(Long id) {
        ConfigVersion current = require(id);
        List<ConfigVersion> all = new ArrayList<>(repository.list(ConfigVersion.class, 1, 10000,
                Collections.<String, Object>singletonMap("targetKey", current.getTargetKey())));
        ConfigVersion previous = all.stream()
                .filter(v -> !v.getId().equals(id) && "PUBLISHED".equals(v.getStatus()))
                .max(Comparator.comparing(ConfigVersion::getId))
                .orElse(null);
        ConfigVersion v = new ConfigVersion();
        v.setEnv(current.getEnv());
        v.setTargetType(current.getTargetType());
        v.setTargetKey(current.getTargetKey());
        v.setVersionNo(nextVersionNo(current.getTargetKey()));
        v.setStatus("PUBLISHED");
        v.setContentJson(previous == null ? current.getContentJson() : previous.getContentJson());
        v.setDraftBy("rollback");
        v.setApproveBy("rollback");
        v.setPublishTime(now());
        v.setRemark("回滚");
        v.setCreateTime(now());
        repository.persist(v);
        if (previous != null) apply(v.getTargetType(), v.getTargetKey(), readContent(v));
        return v;
    }

    public Map<String, Object> diff(Long id) {
        ConfigVersion v = require(id);
        Map<String, Object> res = new HashMap<>();
        res.put("added", readContent(v));
        res.put("removed", Collections.emptyList());
        res.put("modified", Collections.emptyList());
        return res;
    }

    private ConfigVersion require(Long id) {
        ConfigVersion v = repository.find(ConfigVersion.class, id);
        if (v == null) throw new DefineException(BizCode.NOT_FOUND, "版本不存在");
        return v;
    }

    private List<Map<String, Object>> readContent(ConfigVersion v) {
        try {
            return mapper.readValue(v.getContentJson(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new DefineException(BizCode.SYSTEM_ERROR, "版本内容解析失败");
        }
    }

    private void apply(String targetType, String targetKey, List<Map<String, Object>> content) {
        try {
            tagService.batch(targetType, targetKey, content);
        } catch (DefineException e) {
            if (e.getCode() != BizCode.PARAM_ERROR) throw e;
            if (!content.isEmpty()) metaService.save(targetType, content.get(0));
        }
    }
}
