package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_admin_role")
public class AdminRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_code")
    private String roleCode;
    @Column(name = "role_name")
    private String roleName;
    private String remark;
}
