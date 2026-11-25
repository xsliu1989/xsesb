package com.xs.filter.impl;

import com.xs.exception.DefineException;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.filter.Filter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.text.ParseException;
import java.util.Date;

/**
 * 检查时间戳
 */
@Component
@Order(22)
public class Timestamp implements Filter {
    @Override
    public void check(CustomerReq dto) throws DefineException {
        try {
            String timestamp = dto.getTimestamp();
            if (timestamp == null || "".equals(timestamp) || timestamp.length() != 18) {
//            throw new DefineException("9998","时间戳为空或位数不对！");
            } else {
                // 精确到毫秒
                // 获取指定格式的时间
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

                Calendar nowjian10 = Calendar.getInstance();
                nowjian10.setTime(new Date());
                nowjian10.add(Calendar.MINUTE, -10); // 当前时间加10分钟

                Calendar nowjia10 = Calendar.getInstance();
                nowjia10.setTime(new Date());
                nowjia10.add(Calendar.MINUTE, 10); // 当前时间加10分钟

                Calendar _3party = Calendar.getInstance();
                try {
                    _3party.setTime(df.parse(timestamp));
                } catch (ParseException e) {
                    throw new DefineException("9997", "时间戳格式不正确！");
                }

                if (_3party.before(nowjian10)) {
                    // 传上来的时候在当前时间减10分钟之前 失效
                    throw new DefineException("9996", "时间戳失效！");
                } else if (_3party.after(nowjia10)) {
                    // 传上来的时候在当前时间加10分钟之后 失效
                    throw new DefineException("9995", "时间戳失效！");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new DefineException("999", e.getMessage());
        }
    }
}
