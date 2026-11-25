package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_msg_sample")
public class MsgSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "tran_no")
    private String tranNo;
    @Column(name = "sample_type")
    private String sampleType;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private String remark;
    @Column(name = "create_time")
    private String createTime;
}
