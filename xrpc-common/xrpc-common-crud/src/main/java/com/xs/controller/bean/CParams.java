package com.xs.controller.bean;

import lombok.Data;

import java.util.Map;

@Data
public class CParams {
    String pageSize = "10";
    String pageNo = "0";
    String byFiled;      // 根据哪个字段来排序
    String bySort;       //  排序方式
    Map<String,String> params;  // 请求参数

    public String getPageNo(){
        int p = Integer.parseInt(this.pageNo);
        p = (p>0) ? p-1 : p;
        return p+"";
    }
}
