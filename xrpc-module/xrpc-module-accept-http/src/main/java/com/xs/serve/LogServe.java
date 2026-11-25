package com.xs.serve;

import com.xs.datamodel.EsbTranLog;
import com.xs.db.baseservice.impl.CrudBaseServiceImpl;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.tcphttp.dto.CustomerRes;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Scope("prototype")
public class LogServe {

    EsbTranLog esbTranLog=new EsbTranLog();

    @Autowired
    protected CrudBaseServiceImpl baseService;


    public void log(CustomerReq customerReq){


        esbTranLog.setNonce(customerReq.getNonce());
        esbTranLog.setReqchannel(customerReq.getChannel());
        esbTranLog.setTrancode(customerReq.getTranNo());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象，指定时间格式为毫秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        // 格式化时间到毫秒
        String startdate = now.format(formatter);
        esbTranLog.setStartdate(String.valueOf(startdate));

        // 创建DateTimeFormatter对象，指定时间格式为毫秒
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 格式化时间到毫秒
        String date = now.format(formatter1);
        esbTranLog.setTrandate(date);


        esbTranLog.setTranflag(MDC.get("serid"));

        baseService.save(esbTranLog);

    }

    public void log(CustomerRes customerRes){

        esbTranLog.setRes(customerRes.getCode());
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象，指定时间格式为毫秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        // 格式化时间到毫秒
        String enddate = now.format(formatter);
        esbTranLog.setEnddate(String.valueOf(enddate));

        baseService.save(esbTranLog);
    }
}
