package com.xs.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "esb_config_version")
public class ConfigVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String env;
    @Column(name = "target_type")
    private String targetType;
    @Column(name = "target_key")
    private String targetKey;
    @Column(name = "version_no")
    private Integer versionNo;
    private String status;
    @Column(name = "content_json", columnDefinition = "LONGTEXT")
    private String contentJson;
    @Column(name = "diff_json", columnDefinition = "LONGTEXT")
    private String diffJson;
    @Column(name = "draft_by")
    private String draftBy;
    @Column(name = "approve_by")
    private String approveBy;
    @Column(name = "publish_time")
    private String publishTime;
    private String remark;
    @Column(name = "create_time")
    private String createTime;
}
