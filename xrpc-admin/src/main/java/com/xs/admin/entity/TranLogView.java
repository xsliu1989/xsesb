package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * esb_tran_log 的只读视图实体（后台的 EsbTranLog 位于 accept-http 模块，admin 不依赖该模块）。
 */
@Data
@Entity
@Table(name = "esb_tran_log")
public class TranLogView implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nonce;
    @Column(name = "trandate")
    private String tranDate;
    @Column(name = "reqchannel")
    private String reqChannel;
    @Column(name = "trancode")
    private String tranCode;
    @Column(name = "startdate")
    private String startDate;
    @Column(name = "enddate")
    private String endDate;
    private String res;
    @Column(name = "tranflag")
    private String tranFlag;
    @Column(name = "logflag")
    private String logFlag;
}
