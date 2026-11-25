package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_admin_permission")
public class AdminPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "parent_id")
    private Integer parentId;
    @Column(name = "perm_code")
    private String permCode;
    @Column(name = "perm_name")
    private String permName;
    private String type;
    private String path;
    private String component;
    private Integer sort;
}
