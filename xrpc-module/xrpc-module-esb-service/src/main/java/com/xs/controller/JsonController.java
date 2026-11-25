package com.xs.controller;

import com.xs.cache.DataStructureCache;
import com.xs.config.Static;
import com.xs.context.SpringContext;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.menu.LogType;
import com.xs.serve.BeanMapUtil;
import com.xs.serve.LogServe;
import com.xs.serve.LogUtil;
import com.xs.serve.json.JSONObject;
import com.xs.service.Service;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.tcphttp.dto.CustomerRes;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@Data
@Scope(value = "prototype")
@RequestMapping("/controller")
public class JsonController {
    //日志log
    private final static Logger log =  LoggerFactory.getLogger(JsonController.class);



    private  static LogUtil logUtil;

    @PostMapping(value = "jsonHandler/{tranNo}")
    public String handler(@RequestBody String json, @PathVariable("tranNo")String tranNo) {




        String serid = UUID.randomUUID().toString().replace("-", "");
        logUtil.info(LogType.QDREQ, log, "["+tranNo +"]"+ json);


        //获取请求的交易编号
        if(null == tranNo || tranNo.equals("")){
            throw new DefineException("9991", "交接编号不能为空...");
        }

        Dto dto = new Dto();
        dto.setTranNo(tranNo);
        dto.setSerid(serid);

        dto.setData(json);
        try {



            String clazz = DataStructureCache.customerTransactionInfoMap.get(tranNo).getClazz();
            Service defaultAbstractService = (Service) SpringContext.getBean(clazz);
            defaultAbstractService.execute(dto);

        }  catch (DefineException de){
            log.error("处理异常"+ de.getMessage(), de);
        } catch (Exception e) {
            log.error("处理异常"+ e.getMessage(), e);
        }


        return dto.getReturndata();


    }


}
