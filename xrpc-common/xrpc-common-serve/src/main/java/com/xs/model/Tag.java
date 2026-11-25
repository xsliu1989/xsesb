package com.xs.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@MappedSuperclass
public class Tag implements Serializable {

    //标签Key
    @Column(name = "tagname")
    private String tagName;

    //父标签Key
    @Column(name = "parenttag")
    private String parentTag;

    //对应标签Id
    @Column(name = "tagmapping")
    private String tagMapping;

    //字段类型
    @Column(name = "tagtype")
    private String tagType;

    //标签值
    @Column(name = "tagvalue")
    private String tagValue;

    //排序
    @Column(name = "sort")
    private Integer sort;

    // 获得类  目前有listsize,计算list的大小
    @Column(name = "tagflag")
    private String tagflag;

    @Column(name = "taglength")  // 定长的长度
    private Integer taglength;

}
