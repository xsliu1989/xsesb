package com.xs.datamodel;

import com.xs.model.Tag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

//目标系统发送标签表  第三方系统发送报文构建表
@Entity
@Table(name="esb_customer_sysname")
@Data
public class CustomerSysname implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //目标系统接口编号
    @Column(name = "systype")
    private String systype;

    @Column(name = "sysname")
    private String sysname;

    /** flag标志，当前时间渠道是否开启 **/
    @Column(name = "flag")
    private String flag;

    /** 当前渠道并发请求量 **/
    @Column(name = "size")
    private String size;


    @Column(name = "str1")
    private String str1;

    @Column(name = "str2")
    private String str2;

    @Column(name = "str3")
    private String str3;

    @Column(name = "str4")
    private String str4;

    @Column(name = "str5")
    private String str5;


}
