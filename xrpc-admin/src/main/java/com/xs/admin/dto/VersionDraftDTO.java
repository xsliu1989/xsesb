package com.xs.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class VersionDraftDTO implements Serializable {
    private String env;
    private String targetType;
    private String targetKey;
    private List<Map<String, Object>> content;
    private String remark;
}
