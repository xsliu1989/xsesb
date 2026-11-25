package com.xs.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class Dto {

    private String token;           // token验证，非登录业务选填
    private String nonce;           // 随机数 全局流水号
    private String timestamp;       // 时间戳

    private String data;            // AES(业务数据)
    private String returndata;      // 返回数据
    private String key;             // RSA(AESkey)
    private String version;         // 版本号
    private String sign;            // 签名信息
    private String tranNo;          // 业务交易号
    private String smSign;          //验签字段
    private String digestAlgorithm;  // 数字摘要，签名字段
    private String channel;          // 请求渠道
    private String serid;


    private String channelType;      //  渠道请求类型



    private HashMap<String, Object> map = new HashMap<String, Object>();

    private HashMap<String, Object> customer_req_map;
    private HashMap<String, Object> customer_res_map;
    private HashMap<String, Object> provider_req_map;
    private HashMap<String, Object> provider_res_map;

    private Object customerTransactionInfo;
    private Object providerTransactionInfo;

    private String provider_url;


    private String code;              // 编号
    private String message;           // 信息描述
    private String timefly;           // 耗时(毫秒)

}
