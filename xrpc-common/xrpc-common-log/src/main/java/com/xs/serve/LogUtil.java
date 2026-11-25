package com.xs.serve;

import com.xs.cache.BizFlagCache;
import com.xs.menu.LogType;
import org.slf4j.Logger;

import java.util.regex.Pattern;

public  class LogUtil {

    private static Pattern idCardPattern = Pattern.compile("(\\D)(\\d{6})([19,20]\\d{7})(\\d{3}[0-9Xx])(\\D)");
    private static Pattern bankCardPattern = Pattern.compile("(\\D)([3-6]\\d{3})(\\d{8,12})(\\d{4})(\\D)");
    private static Pattern mobilePattern = Pattern.compile("(\\D)([13,14,15,16,17,18,19]\\d{2})(\\d{4})(\\d{4})(\\D)");
    private static Pattern namePattern = Pattern.compile("([^\\u4e00-\\u9fa5])([\\u4e00-\\u9fa5])([\\u4e00-\\u9fa5]{1,3})([^\\u4e00-\\u9fa5])");



   public static void info(LogType flag, Logger logger, String str){

       Object f = BizFlagCache.biz_log_map.get(flag.getIndex());
       if(null == f) {
           Exception e = new Exception("未在logConfig文件中配置" + flag.getIndex());
           logger.error(e.getMessage() ,e);
       } else  if(f.toString().equals("OK")) {
            String s1 = idCardPattern.matcher(str).replaceAll("$1$2********$4$5");
            String s2 = bankCardPattern.matcher(s1).replaceAll("$1$2********$4$5");
            String s3 = mobilePattern.matcher(s2).replaceAll("$1$2****$4$5");
            String endStr = namePattern.matcher(s3).replaceAll("$1$2**$4");

            logger.info(flag.getName() + endStr);
       }
    }


    public static void error(Logger logger, String str, Throwable t){
       logger.error(str, t);
    }

}
