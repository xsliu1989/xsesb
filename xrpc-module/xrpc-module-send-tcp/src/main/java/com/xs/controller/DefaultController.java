package com.xs.controller;

import com.xs.cache.PortMap;
import com.xs.exception.DefineException;
import com.xs.service.impl.ToCService;
import com.xs.serve.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/controller")
public class DefaultController {

    private final static Logger log =  LoggerFactory.getLogger(DefaultController.class);
    private ApplicationContext applicationContext;

    @Autowired
    private ToCService toCService;

    @ResponseBody
    @PostMapping(value = "defaultController")
    public String handler(String tranNo, String data )  {
        log.info("接收数据:"+data);

        String returnval = "";

        try {
            returnval = toCService.sendMess(data, tranNo);
        } catch (DefineException de){
            if(PortMap.cPort_map.get(tranNo).getType().equals("DC")){
                String deStr = de.getCode() + de.getMessage();
                returnval= String.format("%6d",deStr);
            } else if(PortMap.cPort_map.get(tranNo).getType().equals("XML")) {
                String deStr = de.getCode() + de.getMessage();
                returnval= String.format("%6d",deStr);
            }
        }

        log.info("C返回报文："+returnval);
        return returnval;
    }


}
