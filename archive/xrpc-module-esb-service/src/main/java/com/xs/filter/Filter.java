package com.xs.filter;


import com.xs.dto.Dto;

/**
 * 在handle期的过滤器
 */
public interface Filter {
    void  check(Dto dto) throws Exception;

}
