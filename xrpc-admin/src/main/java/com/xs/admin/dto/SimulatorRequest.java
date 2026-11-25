package com.xs.admin.dto;

import lombok.Data;

import java.io.Serializable;

/** 接口模拟器请求：http 与 tcp 共用 */
@Data
public class SimulatorRequest implements Serializable {
    /** HTTP 模式 */
    private String url;
    private String data;
    private String contentType = "application/json;charset=UTF-8";
    private String charset = "UTF-8";
    /** TCP 模式 */
    private String host;
    private Integer port;
    /** 长度头位宽，项目 TCP 固定为 6 */
    private Integer lengthWidth = 6;
    /** 响应读取模式：auto（兼容有/无长度头）/length/raw */
    private String readMode = "auto";
    /** 超时毫秒 */
    private Integer timeout = 5000;
}
