package com.xs.datamodel;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

//目标系统发送标签表  第三方系统发送报文构建表
@Entity
@Table(name="esb_tran_log")
@Data
public class EsbTranLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //目标系统接口编号
    @Column(name = "nonce")
    private String nonce;

    @Column(name = "trandate")
    private String trandate;

    @Column(name = "reqchannel")
    private String reqchannel;

    @Column(name = "trancode")
    private String trancode;

    @Column(name = "startdate")
    private String startdate;

    @Column(name = "enddate")
    private String enddate;

    @Column(name = "res")
    private String res;

    @Column(name = "tranflag")
    private String tranflag;

    @Column(name = "logflag")
    private String logflag;


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
