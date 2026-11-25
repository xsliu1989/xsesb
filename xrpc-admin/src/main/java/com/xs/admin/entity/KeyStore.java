package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_key_store")
public class KeyStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String channel;
    @Column(name = "key_type")
    private String keyType;
    private String fingerprint;
    @Column(name = "cipher_ref")
    private String cipherRef;
    private String status;
    @Column(name = "expire_time")
    private String expireTime;
    @Column(name = "create_time")
    private String createTime;
}
