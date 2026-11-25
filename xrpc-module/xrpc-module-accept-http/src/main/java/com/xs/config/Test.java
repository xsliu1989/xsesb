package com.xs.config;

import java.util.Date;

public class Test {


    /**
     * 每一分钟执行一次的定时任务
     * @param args
     */
    public static void main(String[] args) {

        for (BinFaBean bf : Static.nonce) {

        }

        int  hour1 = new Date().getHours();
        int minute1 = new Date().getMinutes();



        int  hour2 = new Date().getHours();
        int minute2 = new Date().getMinutes();

        // 计算两个Date对象之间的分钟差
        long minutesDiff = (hour2 - hour1) * 60 + (minute2 - minute1);

        System.out.println("两个Date对象之间的分钟差为：" + minutesDiff);
    }
}
