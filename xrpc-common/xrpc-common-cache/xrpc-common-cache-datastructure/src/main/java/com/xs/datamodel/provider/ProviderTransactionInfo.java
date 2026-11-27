package com.xs.datamodel.provider;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Data;

//目标系统表, 第三方系统信息表
@Entity
@Table(name="esb_provider_transaction_info")
@Data
public class ProviderTransactionInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //目标系统接口编号
    @Column(name = "providertranno")
    private String providerTranNo;

    //源系统接口编号
    @Column(name = "customertranno")
    private String customerTranNo;

    //数据格式(json,xml)
    @Column(name = "datatype")
    private String dataType;

    //发送地址
    @Column(name = "sendurl")
    private String sendUrl;

    // 第三方系统名称
    @Column(name = "sysname")
    private String sysName;

    // 使用的编码，统一 UTF-8
    @Column(name = "codeType")
    private String codeType;



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
