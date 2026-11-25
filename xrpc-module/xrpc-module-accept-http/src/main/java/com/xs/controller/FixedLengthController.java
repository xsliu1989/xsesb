package com.xs.controller;


import com.xs.config.Static;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.menu.LogType;
import com.xs.serve.json.JSONObject;
import com.xs.tcphttp.dto.CustomerRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;



@Controller
@RequestMapping("/controller")
public class FixedLengthController  {

    private final static Logger log =  LoggerFactory.getLogger(FixedLengthController.class);

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;

    @ResponseBody
    @PostMapping(value = "fixedLengthHandler")
    public String handler(@RequestBody String fixedLengthStr)  {
        CustomerRes customerRes = new CustomerRes();

        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);
        log.info("接收定长:"+fixedLengthStr);
        try {
//            String requestxml = DocumentHelper.parseText(xml).getRootElement().asXML();

            // 0-10 交易码
            String tranNo =  fixedLengthStr.substring(0,10).trim();

            // 10往后是data
            String data =  fixedLengthStr.substring(10).trim();



            Dto qdto = new Dto();
            qdto.setData(data);
            qdto.setTranNo(tranNo);
            qdto.setChannelType("DC");
            qdto.setSerid(serid);

//            CustomerTranInfo otranInfo = CacheMap.o_traninfo_map.get(customerReq.getTranNo());

            // 从 Nacos 中心获取服务实例信息
//            ServiceInstance serviceInstance = loadBalancerClient.choose("xrpc-module-process");
//            String serviceUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/defaultController";

            Dto rdto = restTemplate.postForObject(Static.processurl,  qdto,  Dto.class);

            String returndata = rdto.getReturndata();

            int length = returndata.length();

            return String.format("%6s",length) + returndata;

        }  catch (ResourceAccessException ce){
            log.error("异常:" + ce.getMessage(), ce);

            return "000108"+ String.format("%8s","9991") + String.format("%100s","PROCESS系统未启动...");
        } catch (DefineException de){
            log.error("异常:"+ de.getMessage(), de);
            return "000108"+ String.format("%8s","9991") + String.format("%100s",de.getMessage());
        } catch (Exception e) {
            log.error("处理异常"+ e.getMessage(), e);
            return "000108"+ String.format("%8s","9991") + String.format("%100s",e.getMessage());
        }

    }

}
