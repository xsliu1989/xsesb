package com.xs.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xs.admin.entity.AdminRole;
import com.xs.admin.entity.AdminUser;
import com.xs.admin.entity.LogSwitch;
import com.xs.admin.repository.GenericRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/** 初始化内置角色、管理员账号与 8 类日志开关（仅当表为空时写入） */
@Component
public class DataInitializer implements ApplicationRunner {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(DataInitializer.class);


    @Autowired
    private GenericRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<AdminRole> roles = repository.list(AdminRole.class, 1, 10, null);
        if (roles.isEmpty()) {
            Arrays.asList(
                    new String[]{"ADMIN", "系统管理员", "全部权限"},
                    new String[]{"CONFIG", "配置员", "配置与草稿"},
                    new String[]{"APPROVER", "审批员", "审批与发布"},
                    new String[]{"OPERATOR", "运维", "日志与监控"},
                    new String[]{"READONLY", "只读用户", "仅查看"}
            ).forEach(r -> {
                AdminRole role = new AdminRole();
                role.setRoleCode(r[0]); role.setRoleName(r[1]); role.setRemark(r[2]);
                repository.persist(role);
            });
        }
        if (repository.findBy(AdminUser.class, "username", "admin").isEmpty()) {
            AdminUser u = new AdminUser();
            u.setUsername("admin");
            u.setRealName("系统管理员");
            u.setStatus("ENABLE");
            u.setRoles("ADMIN");
            u.setPasswordHash(new BCryptPasswordEncoder().encode("admin123"));
            u.setCreateTime(java.time.LocalDateTime.now().toString());
            repository.persist(u);
        }
        if (repository.list(LogSwitch.class, 1, 10, null).isEmpty()) {
            Arrays.asList(
                    new String[]{"01", "OK", "渠道请求"}, new String[]{"02", "OK", "返回渠道"},
                    new String[]{"03", "OK", "第三方请求"}, new String[]{"04", "OK", "第三方返回"},
                    new String[]{"05", "OFF", "转换主程序"}, new String[]{"06", "OFF", "转换辅助"},
                    new String[]{"07", "OK", "耗时"}, new String[]{"08", "OFF", "解析标签"}
            ).forEach(s -> {
                LogSwitch ls = new LogSwitch();
                ls.setLogType(s[0]); ls.setEnabled(s[1]); ls.setRemark(s[2]);
                repository.persist(ls);
            });
        }
    }
}
