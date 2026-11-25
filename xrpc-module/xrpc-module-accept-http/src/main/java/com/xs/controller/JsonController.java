package com.xs.controller;

import com.xs.config.Static;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.menu.LogType;
import com.xs.serve.BeanMapUtil;
import com.xs.serve.LogServe;
import com.xs.serve.json.JSONObject;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.tcphttp.dto.CustomerRes;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.xs.serve.LogUtil;

import java.util.List;
import java.util.UUID;

@RestController
@Data
@Scope(value = "prototype")
@RequestMapping("/controller")
public class JsonController {
    //日志log
    private final static Logger log =  LoggerFactory.getLogger(JsonController.class);

    @Autowired
    private List<Filter> filters;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LogServe logServe;


    private  static LogUtil logUtil;

    @PostMapping(value = "jsonHandler")
    public String handler( @RequestBody String json) {
        CustomerRes customerRes = new CustomerRes();
        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);
        try {
            logUtil.info(LogType.QDREQ, log, json);

            JSONObject jsonObject = new JSONObject(json);
            String jsonStr = jsonObject.get("data").toString();
            jsonObject.remove("data");
            jsonObject.put("data",jsonStr);

            CustomerReq customerReq = BeanMapUtil.mapToBean(jsonObject.toMap(), CustomerReq.class);
            logServe.log(customerReq);

            if(null!=filters) {
                for (Filter checkPre : filters) {
                    checkPre.check(customerReq);
                }
            }

            Dto qdto = new Dto();
            qdto.setData(customerReq.getData());
            qdto.setTranNo(customerReq.getTranNo());
            qdto.setChannelType("JSON");
            qdto.setSerid(serid);
            qdto.setNonce(customerReq.getNonce());


            logUtil.info(LogType.F3REQ, log, customerRes.toString());
            Dto rdto = restTemplate.postForObject(Static.processurl,  qdto,  Dto.class);
            logUtil.info(LogType.F3RES, log, customerRes.toString());

            customerRes.setCode(rdto.getCode());
            customerRes.setMessage(rdto.getMessage());
            customerRes.setData(rdto.getReturndata());

        }  catch (ResourceAccessException ce){
            log.error("异常:" + ce.getMessage(), ce);
            customerRes.setCode("9991");
            customerRes.setData("");
            customerRes.setMessage("PROCESS系统未启动...");
        } catch (DefineException de){
            customerRes.setCode(de.getCode());
            customerRes.setData("");
            customerRes.setMessage(de.getMessage());
        } catch (Exception e) {
            log.error("处理异常"+ e.getMessage(), e);
            customerRes.setCode("9999");
            customerRes.setMessage(e.getMessage());
            customerRes.setData("");
        }

        logServe.log(customerRes);
        String redata = customerRes.getData();
        JSONObject jo = new JSONObject(customerRes);
        if(null != redata && !redata.equals("")){
            jo.put("data", new JSONObject(redata));
        }

        logUtil.info(LogType.QDRES, log, jo.toString());

        return jo.toString();
    }


}
