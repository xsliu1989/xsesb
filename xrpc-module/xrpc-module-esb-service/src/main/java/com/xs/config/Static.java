package com.xs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class Static {

    public static String processurl;
    //日志log
    private final static Logger log =  LoggerFactory.getLogger(Static.class);


    @Value("${url.xrpc-module-process}")
    public void initName(String s){
        processurl = s;
    }


    /** 渠道上送的唯一流水 */
    public static List<BinFaBean> nonce = new ArrayList<BinFaBean>();

    /**
     * 删除  大于3分钟的noce_, 因为nonce还控制着并发量，怕并发量里存在异常未清理的并发
     *
     * 秒（0~59） 例如0/5表示每5秒
     * 分（0~59）
     * 时（0~23）
     * 日（0~31）的某天，需计算
     * 月（0~11）
     * 周几（ 可填1-7 或 SUN/MON/TUE/WED/THU/FRI/SAT）
     */
    @Scheduled(cron = "0 0/3 * * * ? ") //定时任务注解+cron表达式
    public void deleteGq(){

        log.info("定时任务|清理并发|大于5分钟未释放的|开始...");

        for(BinFaBean nonce_ : nonce){
            // 相差时间大于5分钟

            int  hour1 = nonce_.getDate().getHours();
            int  minute1 = nonce_.getDate().getMinutes();

            int  hour2 = new Date().getHours();
            int  minute2 = new Date().getMinutes();

            // 计算两个Date对象之间的分钟差
            long minutesDiff = (hour2 - hour1) * 60 + (minute2 - minute1);

            if(minutesDiff > 3){
                log.info("定时任务|清理并发|大于5分钟未释放的|" + nonce_.getNonce());
                nonce.remove(nonce_);
            }

        }
        log.info("定时任务|清理并发|大于5分钟未释放的|结束...");
    }





}
