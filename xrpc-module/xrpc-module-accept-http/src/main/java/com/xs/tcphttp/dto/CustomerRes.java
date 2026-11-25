package com.xs.tcphttp.dto;


import lombok.Data;

@Data
public class CustomerRes {

    private String resultcode;       // 业务是否成功
    private String resultmessage;    // 业务失败成功描述
    private String code;            // 编号
    private String message;         // 信息描述
    private String timefly;         // 耗时(毫秒)
    private String data;           // AES(业务数据)
    private String key;             // RSA(AESkey)
    private String version;       // 版本号
    private String sign;            // 签名
}
