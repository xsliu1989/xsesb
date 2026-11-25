package com.xs.admin.controller;

import com.xs.admin.common.R;
import com.xs.admin.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin-api/auth")
public class AuthController {

    //日志log
    private final static Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(authService.login(
                String.valueOf(body.get("username")),
                String.valueOf(body.get("password"))));
    }

    @GetMapping("/me")
    public R<Map<String, Object>> me(@AuthenticationPrincipal String username) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(authService.me(username));
    }

    @GetMapping("/permissions")
    public R<?> permissions(@AuthenticationPrincipal String username) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(authService.permissions(username));
    }

    @PostMapping("/logout")
    public R<Boolean> logout() {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(true);
    }
}
