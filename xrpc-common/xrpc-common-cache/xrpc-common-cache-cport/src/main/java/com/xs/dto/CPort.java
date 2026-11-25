package com.xs.dto;

import lombok.Data;

import java.util.List;

//@Entity
//表名
//@Table(name="wc_cport")
@Data
public class CPort {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 交易码
    private String tranno;
    // 端口号
    private Integer port;
    // 字符集
    private String charset;
    // 地址
    private String address;
    // 报文长度的位数 一般是6位够了
    private Integer fixedlength;
    // 接口的类型   定长是DC或者是xml
    private String type;

    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private String str5;
}
