package com.xs.datamodel.provider;

import com.xs.model.Tag;

import javax.persistence.*;

import lombok.Data;

//目标系统发送标签表  第三方系统发送报文构建表
@Entity
@Table(name="esb_provider_request_tag")
@Data
public class ProviderRequestTag extends Tag  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //目标系统接口编号
    @Column(name = "providertranno")
    private String providerTranNo;
//    //标签Key
//    @Column(name = "tag")
//    private String tag;
//    //父标签Key
//    @Column(name = "parenttag")
//    private String parentTag;
//    //对应标签Id
//    @Column(name = "tagindex")
//    private String tagIndex;
//    //字段类型
//    @Column(name = "fieldtype")
//    private String fieldType;
//    //标签值
//    @Column(name = "tagvalue")
//    private String tagValue;
//    //排序
//    private Integer sort;

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
