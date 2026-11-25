package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_log_switch")
public class LogSwitch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "log_type")
    private String logType;
    private String enabled;
    private String remark;
}
