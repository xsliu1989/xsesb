package com.xs.admin.controller;

import com.xs.admin.aop.AuditAction;
import com.xs.admin.common.PageResult;
import com.xs.admin.common.R;
import com.xs.admin.dto.TagDTO;
import com.xs.admin.dto.VersionDraftDTO;
import com.xs.admin.repository.GenericRepository;
import com.xs.admin.service.MetaService;
import com.xs.admin.service.TagService;
import com.xs.admin.service.VersionService;
import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin-api")
public class ConfigController {

    //日志log
    private final static Logger log = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private MetaService metaService;
    @Autowired
    private TagService tagService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private GenericRepository repository;

    // ---------- 通用资源 CRUD ----------

    @GetMapping("/{resource}")
    public R<PageResult<Object>> page(@PathVariable String resource,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam Map<String, Object> params) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(metaService.page(resource, page, size, params));
    }

    @PostMapping("/{resource}")
    @AuditAction(module = "配置中心", action = "新增")
    public R<Object> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(metaService.save(resource, body));
    }

    @PutMapping("/{resource}/{id}")
    @AuditAction(module = "配置中心", action = "修改")
    public R<Object> update(@PathVariable String resource, @PathVariable Integer id,
                            @RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        body.put("id", id);
        return R.ok(metaService.save(resource, body));
    }

    @DeleteMapping("/{resource}/{id}")
    @AuditAction(module = "配置中心", action = "删除")
    public R<Boolean> delete(@PathVariable String resource, @PathVariable Integer id) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        metaService.delete(resource, id);
        return R.ok(true);
    }

    @PatchMapping("/{resource}/{id}/status")
    @AuditAction(module = "配置中心", action = "状态变更")
    public R<Object> status(@PathVariable String resource, @PathVariable Integer id,
                            @RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        metaService.status(resource, id, String.valueOf(body.get("status")));
        return R.ok(repository.find(metaService.clazz(resource), id));
    }

    // ---------- Tag ----------

    @GetMapping("/tags")
    public R<List<TagDTO>> tags(@RequestParam String ownerType, @RequestParam String ownerKey) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(tagService.list(ownerType, ownerKey));
    }

    @PutMapping("/tags/batch")
    @AuditAction(module = "Tag 配置", action = "批量保存")
    public R<Map<String, Object>> tagBatch(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        String ownerType = String.valueOf(body.get("ownerType"));
        String ownerKey = String.valueOf(body.get("ownerKey"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tags = (List<Map<String, Object>>) body.get("tags");
        int saved = tagService.batch(ownerType, ownerKey, tags == null ? Collections.emptyList() : tags);
        return R.ok(Collections.singletonMap("saved", saved));
    }

    @PostMapping("/tags/validate")
    public R<Map<String, Object>> tagValidate(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> raw = (List<Map<String, Object>>) body.get("tags");
        List<TagDTO> tags = new ArrayList<>();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        if (raw != null) raw.forEach(m -> tags.add(mapper.convertValue(m, TagDTO.class)));
        return R.ok(tagService.validate(tags, String.valueOf(body.get("ownerKey")), String.valueOf(body.get("protocol"))));
    }

    @PostMapping("/tags/infer")
    public R<List<TagDTO>> tagInfer(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(tagService.infer(String.valueOf(body.get("sample"))));
    }

    @PostMapping("/tags/copy")
    public R<Map<String, Object>> tagCopy(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        int n = tagService.copy(String.valueOf(body.get("fromOwnerType")), String.valueOf(body.get("fromOwnerKey")),
                String.valueOf(body.get("toOwnerType")), String.valueOf(body.get("toOwnerKey")));
        return R.ok(Collections.singletonMap("copied", n));
    }

    // ---------- 映射 ----------

    @GetMapping("/mapping/compare")
    public R<Map<String, Object>> mapping(@RequestParam String tranNo) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        List<ProviderTransactionInfo> providers = repository.findBy(ProviderTransactionInfo.class, "customerTranNo", tranNo);
        String providerKey = providers.isEmpty() ? tranNo : providers.get(0).getProviderTranNo();
        List<TagDTO> customer = tagService.list("CUSTOMER_REQUEST", tranNo);
        List<TagDTO> provider = tagService.list("PROVIDER_REQUEST", providerKey);
        Map<String, TagDTO> cm = new HashMap<>();
        customer.forEach(t -> cm.put(t.getTagMapping(), t));
        Map<String, TagDTO> pm = new HashMap<>();
        provider.forEach(t -> pm.put(t.getTagMapping(), t));
        List<Map<String, Object>> rows = new ArrayList<>();
        Set<String> keys = new TreeSet<>();
        keys.addAll(cm.keySet()); keys.addAll(pm.keySet());
        keys.forEach(k -> {
            Map<String, Object> row = new HashMap<>();
            row.put("mapping", k);
            row.put("customer", cm.get(k));
            row.put("provider", pm.get(k));
            row.put("status", cm.containsKey(k) && pm.containsKey(k) ? "MAPPED" : (cm.containsKey(k) ? "PROVIDER_MISSING" : "CUSTOMER_MISSING"));
            rows.add(row);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("tranNo", tranNo);
        res.put("rows", rows);
        return R.ok(res);
    }

    // ---------- 版本 ----------

    @GetMapping("/versions")
    public R<PageResult<Object>> versions(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam Map<String, Object> params) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.page(page, size, params));
    }

    @PostMapping("/versions/draft")
    @AuditAction(module = "发布管理", action = "保存草稿")
    public R<Object> draft(@RequestBody VersionDraftDTO dto, @AuthenticationPrincipal String operator) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.draft(dto, operator));
    }

    @PostMapping("/versions/{id}/approve")
    public R<Object> approve(@PathVariable Long id) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.approve(id));
    }

    @PostMapping("/versions/{id}/diff")
    public R<Map<String, Object>> diff(@PathVariable Long id) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.diff(id));
    }

    @PostMapping("/versions/{id}/publish")
    @AuditAction(module = "发布管理", action = "发布")
    public R<Map<String, Object>> publish(@PathVariable Long id, @AuthenticationPrincipal String operator) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.publish(id, operator));
    }

    @PostMapping("/versions/{id}/rollback")
    @AuditAction(module = "发布管理", action = "回滚")
    public R<Object> rollback(@PathVariable Long id) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(versionService.rollback(id));
    }

    // ---------- 看板 ----------

    @GetMapping("/dashboard/overview")
    public R<Map<String, Object>> overview() {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        Map<String, Object> res = new HashMap<>();
        res.put("channelCount", repository.count(CustomerSysname.class, null));
        res.put("customerTranCount", repository.count(CustomerTransactionInfo.class, null));
        res.put("providerTranCount", repository.count(ProviderTransactionInfo.class, null));
        res.put("openedCount", repository.list(CustomerTransactionInfo.class, 1, 10000, null).stream()
                .filter(t -> "true".equals(t.getIsOpen())).count());
        res.put("todayCalls", 0);
        res.put("successRate", "-");
        res.put("recentChanges", repository.list(com.xs.admin.entity.ConfigVersion.class, 1, 5, null));
        res.put("errorTop", Collections.emptyList());
        return R.ok(res);
    }
}
