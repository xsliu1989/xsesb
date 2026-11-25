package com.xs.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TagDTO implements Serializable {
    private Integer id;
    private String tagName;
    private String parentTag;
    private String tagMapping;
    private String tagType;
    private String tagValue;
    private Integer sort;
    private String tagflag;
    private Integer taglength;
}
