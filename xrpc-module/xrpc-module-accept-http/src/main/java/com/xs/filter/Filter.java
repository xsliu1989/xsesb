package com.xs.filter;


import com.xs.tcphttp.dto.*;

/**
 * 在handle期的过滤器
 */
public interface Filter {
    void  check(CustomerReq dto) throws Exception;

}
