package com.xs.serve;


import com.xs.exception.DefineException;
import com.xs.serve.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanJsonUtil {

    //日志log
    private final static Logger log =  LoggerFactory.getLogger(BeanJsonUtil.class);


    public static Object jsonStrToBean(String jsonStr, Class clazz) throws DefineException {


        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return  BeanMapUtil.mapToBean(jsonObject.toMap(), clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }
}
