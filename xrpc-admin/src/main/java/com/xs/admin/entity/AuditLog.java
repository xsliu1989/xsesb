package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_admin_audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operator;
    @Column(name = "operate_time")
    private String operateTime;
    private String ip;
    private String module;
    private String action;
    private String target;
    @Column(name = "before_json", columnDefinition = "TEXT")
    private String beforeJson;
    @Column(name = "after_json", columnDefinition = "TEXT")
    private String afterJson;
    private String result;
}
