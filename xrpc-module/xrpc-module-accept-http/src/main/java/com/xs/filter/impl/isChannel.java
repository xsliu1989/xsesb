package com.xs.filter.impl;

import com.xs.cache.DataStructureCache;
import com.xs.controller.JsonController;
import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.exception.DefineException;
import com.xs.serve.StructureUtil;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.filter.Filter;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 接口是否是允许的渠道
 * 检查渠道是否允许上送
 */
@Component
@Order(3)
public class isChannel implements Filter {

    private final static Logger log =  LoggerFactory.getLogger(JsonController.class);

    @Override
    public void check(CustomerReq dto) throws DefineException {

        try {
            String tranNo = dto.getTranNo();
            // 接口信息
            CustomerTransactionInfo customerTransactionInfo = StructureUtil.getCusInfo(tranNo);
            String sysname = customerTransactionInfo.getSysName();
            //
            CustomerSysname customerSysname = StructureUtil.getConnectCountByChannel(sysname);

            // 首先判断渠道有没有被禁止
            if(customerSysname.getFlag().equals("false")) {
                throw new DefineException("0999", "禁止访问，渠道: "+ customerSysname.getSysname());
            }


            // 判断当前的接口，是否允许渠道访问
            if(null!= dto.getChannel() && sysname.indexOf(dto.getChannel()) >= 0){   // 是

            } else {
                throw new DefineException("9999", "非允许渠道");
            }
        } catch (Exception e) {
            log.error("请检查渠道名称是否配置",e);
            throw new DefineException("999", e.getMessage());
        }

    }
}
