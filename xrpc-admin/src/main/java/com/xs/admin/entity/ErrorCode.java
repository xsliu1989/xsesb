package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_error_code")
public class ErrorCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String message;
    private String module;
    private String suggestion;
    private String status;
}
