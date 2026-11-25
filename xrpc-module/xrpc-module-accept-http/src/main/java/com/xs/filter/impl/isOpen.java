package com.xs.filter.impl;

import com.xs.cache.DataStructureCache;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.serve.StructureUtil;
import com.xs.tcphttp.dto.CustomerReq;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 商品是否上下架
 */
@Component
@Order(50)
public class isOpen implements Filter {
    @Override
    public void check(CustomerReq dto) throws Exception {
        String tranNo = dto.getTranNo();
        CustomerTransactionInfo customerTransactionInfo = StructureUtil.getCusInfo(tranNo);
        if(customerTransactionInfo == null){
            throw new DefineException("98935", "接口未上架！");
        }
        // 需要对报文进行加解密操作
        if(null == customerTransactionInfo.getIsOpen() || customerTransactionInfo.getIsOpen().equals("false")){
            throw new DefineException("9895", "接口未上架！");
        }
    }
}
