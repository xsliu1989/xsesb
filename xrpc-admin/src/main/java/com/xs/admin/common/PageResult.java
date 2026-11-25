package com.xs.admin.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private int page;
    private int size;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(int page, int size, long total, List<T> records) {
        PageResult<T> r = new PageResult<>();
        r.setPage(page);
        r.setSize(size);
        r.setTotal(total);
        r.setRecords(records);
        return r;
    }
}
