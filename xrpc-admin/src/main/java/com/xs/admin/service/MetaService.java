package com.xs.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xs.admin.common.BizCode;
import com.xs.admin.common.DefineException;
import com.xs.admin.common.PageResult;
import com.xs.admin.entity.*;
import com.xs.admin.repository.GenericRepository;
import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MetaService {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(MetaService.class);


    @Autowired
    private GenericRepository repository;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Map<String, Class<?>> CLASSES = new HashMap<>();
    private static final Map<String, String> STATUS_FIELD = new HashMap<>();

    static {
        CLASSES.put("channels", CustomerSysname.class);
        CLASSES.put("customer-transactions", CustomerTransactionInfo.class);
        CLASSES.put("provider-transactions", ProviderTransactionInfo.class);
        CLASSES.put("samples", MsgSample.class);
        CLASSES.put("error-codes", ErrorCode.class);
        CLASSES.put("cport", CPortConfig.class);
        CLASSES.put("keys", KeyStore.class);
        CLASSES.put("users", AdminUser.class);
        CLASSES.put("roles", AdminRole.class);
        CLASSES.put("audit-logs", AuditLog.class);
        CLASSES.put("log-switches", LogSwitch.class);

        STATUS_FIELD.put("channels", "flag");
        STATUS_FIELD.put("customer-transactions", "isOpen");
        STATUS_FIELD.put("error-codes", "status");
        STATUS_FIELD.put("keys", "status");
        STATUS_FIELD.put("users", "status");
        STATUS_FIELD.put("log-switches", "enabled");
    }

    public Class<?> clazz(String resource) {
        Class<?> c = CLASSES.get(resource);
        if (c == null) throw new DefineException(BizCode.PARAM_ERROR, "未知资源：" + resource);
        return c;
    }

    public PageResult<Object> page(String resource, int page, int size, Map<String, Object> filters) {
        Class<?> c = clazz(resource);
        long total = repository.count(c, filters);
        List<?> rows = repository.list(c, page, size, filters);
        return PageResult.of(page, size, total, new ArrayList<>(rows));
    }

    public List<Object> list(String resource, Map<String, Object> filters) {
        return new ArrayList<>(repository.list(clazz(resource), 1, 10000, filters));
    }

    public Object save(String resource, Map<String, Object> data) {
        Class<?> c = clazz(resource);
        Object entity = mapper.convertValue(data, c);
        return repository.save(entity);
    }

    public void delete(String resource, Object id) {
        repository.delete(clazz(resource), id);
    }

    public void status(String resource, Object id, String status) {
        Class<?> c = clazz(resource);
        Object entity = repository.find(c, id);
        if (entity == null) throw new DefineException(BizCode.NOT_FOUND, "记录不存在");
        String field = STATUS_FIELD.getOrDefault(resource, "status");
        try {
            java.lang.reflect.Method setter = c.getMethod("set" + Character.toUpperCase(field.charAt(0)) + field.substring(1), String.class);
            setter.invoke(entity, status);
        } catch (Exception e) {
            throw new DefineException(BizCode.SYSTEM_ERROR, "更新状态失败：" + e.getMessage());
        }
        repository.save(entity);
    }
}
