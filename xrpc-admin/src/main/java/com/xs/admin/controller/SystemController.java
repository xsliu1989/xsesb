package com.xs.admin.controller;

import com.xs.admin.common.PageResult;
import com.xs.admin.common.R;
import com.xs.admin.entity.LogSwitch;
import com.xs.admin.entity.TranLogView;
import com.xs.admin.repository.GenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin-api")
public class SystemController {

    //日志log
    private final static Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private GenericRepository repository;

    @Value("${esb.refresh.endpoints:}")
    private String refreshEndpoints;

    @Value("${esb.effect-mode:RESTART_REQUIRED}")
    private String effectMode;

    @GetMapping("/cache/status")
    public R<Map<String, Object>> cacheStatus() {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        Map<String, Object> res = new HashMap<>();
        res.put("cacheName", "map");
        res.put("effectMode", effectMode);
        res.put("refreshEndpoints", refreshEndpoints.isEmpty() ? Collections.emptyList() : Arrays.asList(refreshEndpoints.split(",")));
        res.put("hashes", Collections.emptyList());
        return R.ok(res);
    }

    /**
     * 方案 A：仅返回提示；方案 B：此处遍历 refreshEndpoints 调用各 ESB 实例的 /internal/refresh。
     * 接口契约见 08-前台管理系统-详细设计.md 第 4.5 节。
     */
    @PostMapping("/cache/refresh")
    public R<Map<String, Object>> refresh() {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        Map<String, Object> res = new HashMap<>();
        boolean refreshed = !refreshEndpoints.isEmpty();
        res.put("refreshed", refreshed);
        res.put("effectMode", effectMode);
        res.put("message", refreshed ? "已通知 ESB 实例刷新" : "当前为方案 A，请重启 ESB 进程使配置生效");
        return R.ok(res);
    }

    @GetMapping("/log-switches")
    public R<List<LogSwitch>> logSwitches() {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        return R.ok(new ArrayList<>(repository.list(LogSwitch.class, 1, 100, null)));
    }

    @PutMapping("/log-switches/{logType}")
    public R<LogSwitch> saveLogSwitch(@PathVariable String logType, @RequestBody LogSwitch body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        LogSwitch row = repository.findBy(LogSwitch.class, "logType", logType).stream().findFirst().orElse(null);
        if (row == null) {
            row = new LogSwitch();
            row.setLogType(logType);
        }
        row.setEnabled(body.getEnabled());
        return R.ok(repository.save(row));
    }

    @GetMapping("/tran-logs")
    public R<PageResult<Object>> logs(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam Map<String, Object> params) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        long total = repository.count(TranLogView.class, params);
        return R.ok(PageResult.of(page, size, total,
                new ArrayList<>(repository.list(TranLogView.class, page, size, params))));
    }

    @GetMapping("/tran-logs/{serid}")
    public R<Map<String, Object>> logDetail(@PathVariable String serid) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        List<TranLogView> rows = repository.findBy(TranLogView.class, "tranFlag", serid);
        Map<String, Object> res = new HashMap<>();
        res.put("row", rows.isEmpty() ? null : rows.get(0));
        res.put("request", "");
        res.put("response", "");
        res.put("stages", Collections.emptyList());
        return R.ok(res);
    }

    /** 在线调试：本地复现四段式转换（dryRun=true）；dryRun=false 需接入真实出站。 */
    @PostMapping("/debug/invoke")
    public R<Map<String, Object>> debug(@RequestBody Map<String, Object> body) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        String data = String.valueOf(body.get("data"));
        Map<String, Object> parsed;
        try {
            parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(data, Map.class);
        } catch (Exception e) {
            parsed = Collections.singletonMap("parseError", e.getMessage());
        }
        List<Map<String, Object>> stages = new ArrayList<>();
        stage(stages, "原始Map", parsed);
        stage(stages, "customer_req_map", parsed);
        stage(stages, "provider_req_map", parsed);
        stage(stages, "目标报文", parsed);
        stage(stages, "provider_res_map", Collections.singletonMap("response", "（本地调试未调用数据源）"));
        stage(stages, "customer_res_map", Collections.singletonMap("response", "（本地调试未调用数据源）"));
        Map<String, Object> res = new HashMap<>();
        res.put("stages", stages);
        res.put("response", "");
        res.put("costMs", 0);
        res.put("errorCode", null);
        return R.ok(res);
    }

    private void stage(List<Map<String, Object>> stages, String name, Object content) {
        Map<String, Object> s = new HashMap<>();
        s.put("name", name);
        s.put("content", content);
        stages.add(s);
    }
}
