package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_cport_config")
public class CPortConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String trannos;
    private String address;
    private Integer port;
    private String charset;
    @Column(name = "fixedlength")
    private Integer fixedLength;
    private String type;
    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private String str5;
}
