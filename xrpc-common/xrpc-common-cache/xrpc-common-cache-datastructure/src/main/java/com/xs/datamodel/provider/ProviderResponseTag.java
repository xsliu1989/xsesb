package com.xs.datamodel.provider;

import com.xs.model.Tag;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 【源系统或 客户端系统 或 渠道系统的】 响应标签表
 *
 * 请求处理完成之后，组织返回给源系统的响应数据时，
 * 使用当前响应标签表 ，去组织响应的数据结构。
 *
 *
 */

@Entity
@Table(name="esb_provider_response_tag")
@Data
public class ProviderResponseTag extends Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //源系统接口编号
    @Column(name = "providertranno")
    private String providertranno;

//    //标签Key
//    private String tagName;
//
//    //父标签Key
//    @Column(name = "parenttag")
//    private String parentTag;
//
//    //对应标签ID
//    @Column(name = "tagindex")
//    private String tagIndex;
//
//    //字段类型
//    @Column(name = "fieldtype")
//    private String fieldType;
//
//    //标签值-固定值
//    @Column(name = "tagvalue")
//    private String tagValue;
//
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

//    @Transient
//    private List<ProviderResponseTag> children; //下级目录
}
