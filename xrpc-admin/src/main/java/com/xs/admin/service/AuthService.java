package com.xs.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xs.admin.common.BizCode;
import com.xs.admin.common.DefineException;
import com.xs.admin.entity.AdminUser;
import com.xs.admin.repository.GenericRepository;
import com.xs.admin.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AuthService {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(AuthService.class);


    @Autowired
    private GenericRepository repository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Map<String, Object> login(String username, String password) {
        List<AdminUser> users = repository.findBy(AdminUser.class, "username", username);
        if (users.isEmpty()) throw new DefineException(BizCode.UNAUTHORIZED, "账号或密码错误");
        AdminUser u = users.get(0);
        if (!"ENABLE".equals(u.getStatus()) || !encoder.matches(password, u.getPasswordHash())) {
            throw new DefineException(BizCode.UNAUTHORIZED, "账号或密码错误");
        }
        u.setLastLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        repository.save(u);
        Map<String, Object> res = new HashMap<>();
        res.put("token", jwtUtil.generate(username));
        res.put("username", username);
        res.put("realName", u.getRealName());
        return res;
    }

    public Map<String, Object> me(String username) {
        List<AdminUser> users = repository.findBy(AdminUser.class, "username", username);
        Map<String, Object> res = new HashMap<>();
        res.put("username", username);
        res.put("realName", users.isEmpty() ? username : users.get(0).getRealName());
        return res;
    }

    public List<String> permissions(String username) {
        List<AdminUser> users = repository.findBy(AdminUser.class, "username", username);
        if (users.isEmpty() || users.get(0).getRoles() == null) return Collections.emptyList();
        if (users.get(0).getRoles().contains("ADMIN")) return Collections.singletonList("*");
        return Arrays.asList(users.get(0).getRoles().split(","));
    }
}
