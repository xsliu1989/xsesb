package com.xs.datamodel.customer;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 【源系统或 客户端系统 或 渠道系统】 的  接口信息
 *
 *
 */

@Entity
@Table(name="esb_customer_transaction_info")
@Data
public class CustomerTransactionInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //接口编号
    @Column(name = "tranno")
    private String tranNo;

//    //数据格式(JSON,XML)
//    @Column(name = "datatype")
//    private String dataType;

    //前端系统名称 (多系统逗号隔开)
    @Column(name = "sysname")
    private String sysName;

    //对该接口的处理，具体由哪个类，或者哪个服务处理
    @Column(name = "clazz")
    private String clazz;

    //是否加解密 true 需要加解密  flase不需要
    @Column(name = "iscryption")
    private String isCryption;

    //接口是否上架 true 已上架  false 已下架
    @Column(name = "isOpen")
    private String isOpen;

    // 渠道端的接口并发数。
    @Column(name = "size")
    private String size;

    // 接口并发量
    @Column(name = "str1")
    private String str1;

    @Column(name = "str2")
    private String str2;

    @Column(name = "str3")
    private String str3;

    @Column(name = "str4")
    private String str4;

    @Column(name = "str5")
    private String str5;


}
