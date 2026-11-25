package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_admin_user")
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    @Column(name = "password_hash")
    private String passwordHash;
    private String salt;
    @Column(name = "real_name")
    private String realName;
    private String status;
    @Column(name = "last_login_time")
    private String lastLoginTime;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private String updateTime;
    /** 逗号分隔的角色编码，便于演示；正式环境使用 esb_admin_user_role */
    private String roles;
}
