package com.xs.admin.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xs.admin.entity.AuditLog;
import com.xs.admin.repository.GenericRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Aspect
@Component
public class AuditAspect {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(AuditAspect.class);


    @Autowired
    private GenericRepository repository;

    @Around("@annotation(audit)")
    public Object around(ProceedingJoinPoint pjp, AuditAction audit) throws Throwable {
        Object result = pjp.proceed();
        try {
            AuditLog log = new AuditLog();
            Object[] args = pjp.getArgs();
            Object first = args.length > 0 ? args[0] : null;
            String target = "";
            String before = "";
            String after = "";
            if (first instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) first;
                Object key = map.get("targetKey");
                target = key != null ? String.valueOf(key) : String.valueOf(map.get("id"));
                after = mask(map.toString());
            } else if (first != null) {
                target = String.valueOf(first);
            }
            log.setOperator(currentUser());
            log.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log.setIp("");
            log.setModule(audit.module());
            log.setAction(audit.action());
            log.setTarget(target);
            log.setBeforeJson(before);
            log.setAfterJson(after);
            log.setResult("SUCCESS");
            repository.persist(log);
        } catch (Exception ignored) {
            // 审计失败不影响业务
        }
        return result;
    }

    private String mask(String s) {
        return s.replaceAll("(?i)(password|privateKey|secret)[^,}\\]]*", "******");
    }

    private String currentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : auth.getName();
    }
}
