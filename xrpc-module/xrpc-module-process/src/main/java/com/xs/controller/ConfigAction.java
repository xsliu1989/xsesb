package com.xs.controller;


import com.xs.cache.DataStructureCache;
import com.xs.context.SpringContext;
import com.xs.menu.LogType;
import com.xs.service.DefaultAbstractService;
import com.xs.service.DefaultAbstractServiceRedis;
import com.xs.service.Service;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Scope;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.xs.serve.LogUtil;

@RestController
@Scope(value = "prototype")
public class ConfigAction {

    private final static Logger log = (Logger) LoggerFactory.getLogger(ConfigAction.class);

    private  static LogUtil logUtil;

//    @Autowired
//    List<DefaultAbstractService> defaultAbstractServices;

    @ResponseBody
    @PostMapping(value = "defaultController")
    public Dto execute(@RequestBody(required = false)Dto dto) throws Exception {


        MDC.put("serid", dto.getSerid());
        logUtil.info(LogType.QDREQ, log, "["+dto.getTranNo() +"]"+dto.toString());


        //获取请求的交易编号
        String tranNo = dto.getTranNo();
        if(null == tranNo || tranNo.equals("")){
            throw new DefineException("9991", "交接编号不能为空...");
        }

        try {
            String clazz = DataStructureCache.customerTransactionInfoMap.get(dto.getTranNo()).getClazz();
            Service defaultAbstractService = (Service) SpringContext.getBean(clazz);
            defaultAbstractService.execute(dto);
        } catch (DefineException de){
            log.error("异常:"+ de.getMessage(), de);
            dto.setCode(de.getCode());
            dto.setData("");
            dto.setMessage(de.getMessage());
        } catch (Exception e) {
            log.error("处理异常"+ e.getMessage(), e);
            dto.setCode("3453");
            dto.setMessage(e.getMessage());
            dto.setData("");
        }

        logUtil.info(LogType.QDRES, log, dto.toString());

        return dto;

    }

//    public DefaultAbstractService getService(String tranNo){
//        for (DefaultAbstractService defaultAbstractService: defaultAbstractServices) {
//            if(defaultAbstractService.support(tranNo)){
//                return defaultAbstractService;
//            }
//        }
//        return null;
//    }




//    public static String getLocalIP() {
//        String localIP = "127.0.0.1";
//        try {
//            OK:
//            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
//                NetworkInterface networkInterface = interfaces.nextElement();
//                //过滤非物理网卡或无用网 networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()
//                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
//                    continue;
//                }
//                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
//                while (addresses.hasMoreElements()) {
//                    InetAddress address = addresses.nextElement();
//                    if (address instanceof Inet4Address) {
//                        localIP = address.getHostAddress();
//                        break OK;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            return localIP;
//        }
//        return localIP;
//    }


}
