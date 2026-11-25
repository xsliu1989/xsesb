package com.xs.controller;


import com.xs.config.Static;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.menu.LogType;
import com.xs.serve.BeanMapUtil;
import com.xs.serve.LogUtil;
import com.xs.serve.json.JSONObject;
import com.xs.serve.json.XML;
import com.xs.tcphttp.dto.CustomerReq;
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

import java.net.ConnectException;
import java.util.*;

@Controller
@RequestMapping("/controller")
public class XmlController  {

    private final static Logger log =  LoggerFactory.getLogger(XmlController.class);
    private  static LogUtil logUtil;

    @Autowired
    private List<Filter> filters;

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;

    @ResponseBody
    @PostMapping(value = "xmlHandler")
    public String handler(@RequestBody String xml) {
        CustomerRes customerRes = new CustomerRes();

        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);
        try {

            logUtil.info(LogType.QDREQ, log, xml);

//            String requestxml = DocumentHelper.parseText(xml).getRootElement().asXML();

            JSONObject requestJson = XML.toJSONObject(xml, true);
            String oname= JSONObject.getNames(requestJson)[0];
            JSONObject message = (JSONObject) requestJson.get(oname);
            JSONObject data = (JSONObject) message.get("data");
            String tranNo = message.get("tranNo").toString();
            message.remove("data");
            message.put("data",XML.toString(data));

            CustomerReq customerReq = BeanMapUtil.mapToBean(message.toMap(), CustomerReq.class);

            if(null!=filters) {
                for (Filter checkPre : filters) {
                    checkPre.check(customerReq);
                }
            }


            Dto qdto = new Dto();
            qdto.setData(XML.toString(data));
            qdto.setTranNo(tranNo);
            qdto.setChannelType("XML");
            qdto.setSerid(serid);

            Dto rdto = restTemplate.postForObject(Static.processurl, qdto, Dto.class);

            customerRes.setCode(rdto.getCode());
            customerRes.setMessage(rdto.getMessage());
            customerRes.setData(rdto.getReturndata());



        } catch (ResourceAccessException ce){
            log.error("异常:" + ce.getMessage(), ce);
            customerRes.setCode("9991");
            customerRes.setData("");
            customerRes.setMessage("PROCESS系统未启动...");
        } catch (DefineException de) {
            log.error("异常:" + de.getMessage(), de);
            customerRes.setCode(de.getCode());
            customerRes.setData("");
            customerRes.setMessage(de.getMessage());
        } catch (Exception e) {
            log.error("处理异常" + e.getMessage(), e);
            customerRes.setCode("9999");
            customerRes.setMessage(e.getMessage());
            customerRes.setData("");
        }

        String redata = customerRes.getData();
        JSONObject jo = new JSONObject(customerRes);
        if(redata != null) {
            JSONObject xmlredata = XML.toJSONObject(redata, true);
            jo.remove("data");
            jo.put("data", xmlredata);
        }
        String returnXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <transaction>" + XML.toString(jo) + "</transaction>";

        logUtil.info(LogType.QDRES, log, returnXml);

        return returnXml;

    }

}
