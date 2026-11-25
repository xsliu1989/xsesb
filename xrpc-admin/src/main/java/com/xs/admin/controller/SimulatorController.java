package com.xs.admin.controller;

import com.xs.admin.common.R;
import com.xs.admin.dto.SimulatorRequest;
import com.xs.admin.dto.SimulatorResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 接口模拟器后端：浏览器无法直接发原始 TCP，因此 TCP 由 admin 代理；
 * HTTP 也走代理，避免前端跨域问题。
 */
@RestController
@RequestMapping("/admin-api/simulator")
public class SimulatorController {

    //日志log
    private final static Logger log = LoggerFactory.getLogger(SimulatorController.class);

    @Operation(summary = "模拟渠道 HTTP 发报")
    @PostMapping("/http")
    public R<SimulatorResponse> http(@RequestBody SimulatorRequest req) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        long start = System.currentTimeMillis();
        SimulatorResponse res = new SimulatorResponse();
        HttpURLConnection conn = null;
        try {
            String url = req.getUrl();
            if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
                throw new IllegalArgumentException("URL 必须以 http:// 或 https:// 开头");
            }
            String cs = resolveCharset(req.getCharset());
            int timeout = req.getTimeout() == null ? 5000 : req.getTimeout();
            String contentType = req.getContentType() == null ? "application/json;charset=UTF-8" : req.getContentType();

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Accept", "*/*");

            byte[] payload = (req.getData() == null ? "" : req.getData()).getBytes(Charset.forName(cs));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload);
                os.flush();
            }

            int status = conn.getResponseCode();
            InputStream is = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String body = readAll(is, cs);

            Map<String, String> headers = new LinkedHashMap<>();
            conn.getHeaderFields().forEach((k, v) -> {
                if (k != null && v != null && !v.isEmpty()) headers.put(k, v.get(0));
            });

            res.setSuccess(status >= 200 && status < 300);
            res.setStatus(status);
            res.setBody(body);
            res.setHeaders(headers);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setError(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
            res.setCostMs(System.currentTimeMillis() - start);
        }
        return R.ok(res);
    }

    @Operation(summary = "模拟渠道 TCP 发报（[N位长度][报文]）")
    @PostMapping("/tcp")
    public R<SimulatorResponse> tcp(@RequestBody SimulatorRequest req) {
        MDC.put("serid", UUID.randomUUID().toString().replace("-", ""));
        long start = System.currentTimeMillis();
        SimulatorResponse res = new SimulatorResponse();
        Socket socket = new Socket();
        try {
            String host = req.getHost() == null ? "127.0.0.1" : req.getHost();
            int port = req.getPort() == null ? 9999 : req.getPort();
            int width = req.getLengthWidth() == null ? 6 : req.getLengthWidth();
            int timeout = req.getTimeout() == null ? 5000 : req.getTimeout();
            String cs = resolveCharset(req.getCharset());
            String mode = req.getReadMode() == null ? "auto" : req.getReadMode();

            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);

            byte[] payload = (req.getData() == null ? "" : req.getData()).getBytes(Charset.forName(cs));
            String head = String.format("%0" + width + "d", payload.length);
            OutputStream out = socket.getOutputStream();
            out.write(head.getBytes(StandardCharsets.US_ASCII));
            out.write(payload);
            out.flush();

            byte[] raw = readUntilEnd(socket.getInputStream(), timeout);
            String body = new String(raw, Charset.forName(cs));
            body = stripLengthHeader(raw, body, width, cs, mode);

            res.setSuccess(true);
            res.setStatus(200);
            res.setBody(body);
            Map<String, String> headers = new HashMap<>();
            headers.put("frame", "[" + width + "位长度][报文]");
            headers.put("sentBytes", String.valueOf(payload.length));
            headers.put("receivedBytes", String.valueOf(raw.length));
            res.setHeaders(headers);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setError(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            try { socket.close(); } catch (Exception ignored) { }
            res.setCostMs(System.currentTimeMillis() - start);
        }
        return R.ok(res);
    }

    /** 兼容当前 TCP 接入端响应不带长度头的情况 */
    private String stripLengthHeader(byte[] raw, String defaultBody, int width, String cs, String mode) {
        if ("raw".equals(mode)) return defaultBody;
        if (raw.length <= width) return defaultBody;
        String lenStr = new String(raw, 0, width, StandardCharsets.US_ASCII).trim();
        if (!lenStr.matches("\\d+")) return defaultBody;
        int len;
        try { len = Integer.parseInt(lenStr); } catch (NumberFormatException e) { return defaultBody; }
        if (len < 0 || len > raw.length - width) return defaultBody;
        if ("length".equals(mode) && len != raw.length - width) return defaultBody;
        return new String(raw, width, len, Charset.forName(cs));
    }

    /** 读到 EOF 或超时为止；当前 TCP 接入端发完即关，所以通常能正常结束 */
    private byte[] readUntilEnd(InputStream in, int timeout) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        long deadline = System.currentTimeMillis() + timeout;
        try {
            int n;
            while ((n = in.read(buf)) != -1) {
                bos.write(buf, 0, n);
                if (System.currentTimeMillis() > deadline) break;
            }
        } catch (SocketTimeoutException ignored) {
            // 对端未关闭连接时按超时处理，返回已收到的数据
        }
        return bos.toByteArray();
    }

    private String readAll(InputStream in, String cs) throws IOException {
        if (in == null) return "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
        return new String(bos.toByteArray(), Charset.forName(cs));
    }

    private String resolveCharset(String cs) {
        if (cs == null || cs.trim().isEmpty()) return "UTF-8";
        return cs.trim();
    }
}
