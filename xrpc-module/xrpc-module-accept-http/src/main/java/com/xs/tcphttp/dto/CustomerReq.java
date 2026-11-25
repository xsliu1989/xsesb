package com.xs.tcphttp.dto;

import lombok.Data;


@Data
public class CustomerReq {

    private String tranNo;          // 业务交易号
    private String token;           // token验证，非登录业务选填
    private String nonce;           // 随机数(全局唯一)(格式：16位时间戳+3位随机码)
    private String timestamp;       // 时间戳（格式16位）(用来检验是否是5分钟之内的请求)
    private String channel;         // 渠道标识
    private String digestAlgorithm; // 数据摘要 （为了保证数据一致性，要把业务数据进行压缩）

//    private String key;             // RSA(AESkey)
//    private String version;         // 版本号
//    private String sign;            // 签名信息
//    private String smSign;          // 验签字段

    private String data;            // AES(业务数据)
}
