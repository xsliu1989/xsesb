package com.xs.controller;

import com.xs.config.Static;
import com.xs.exception.DefineException;
import com.xs.tcphttp.dto.*;
import com.xs.filter.Filter;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.xs.dto.Dto;


import java.util.List;
import java.util.UUID;

@RestController
@Data
@RequestMapping("/controller")
public class DefaultController {
    //日志log
    private final static Logger log =  LoggerFactory.getLogger(DefaultController.class);

    @Autowired
    private List<Filter> filters;
//    @Autowired
//    private List<Action> actions;

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;



    @PostMapping(value = "defaultHandler")
    public CustomerRes handler(CustomerReq customerReq) {
        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);




        CustomerRes customerRes = new CustomerRes();
        try {
            if(null!=filters) {
                for (Filter checkPre : filters) {
                    checkPre.check(customerReq);
                }
            }

            Dto qdto = new Dto();
            qdto.setData(customerReq.getData());
            qdto.setTranNo(customerReq.getTranNo());

//            CustomerTranInfo otranInfo = CacheMap.o_traninfo_map.get(customerReq.getTranNo());

            // 从 Nacos 中心获取服务实例信息
//            ServiceInstance serviceInstance = loadBalancerClient.choose("xrpc-module-process");
//            String serviceUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/defaultController";

            Dto rdto = restTemplate.postForObject(Static.processurl,  qdto,  Dto.class);



            customerRes.setCode(rdto.getCode());
            customerRes.setMessage(rdto.getMessage());
            customerRes.setData(rdto.getReturndata());

        } catch (DefineException de){
            log.info("处理异常", de);
            customerRes.setCode(de.getCode());
            customerRes.setData("");
            customerRes.setMessage(de.getMessage());
        } catch (Exception e) {
            log.info("处理异常", e);
            customerRes.setCode("9999");
            customerRes.setMessage(e.getMessage());
            customerRes.setData("");
        }
        log.info("handel返回报文:"+customerRes.toString());
        return customerRes;
    }






}
