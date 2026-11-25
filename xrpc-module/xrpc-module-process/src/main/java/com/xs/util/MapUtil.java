package com.xs.util;

import com.xs.exception.DefineException;
import com.xs.serve.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class MapUtil {

    private final static Logger log = (Logger) LoggerFactory.getLogger(MapUtil.class);

    private  static LogUtil logUtil;


    /**
     * 从map里逐层取值。
     * @param map
     * @param key
     * @param defaluVal
     * @return
     */
    public static Object getVal(HashMap map, String key, String defaluVal) {
        try {
            if (key == null) {
                return defaluVal;
            }
            String[] keys = key.split("\\.");
            for (int i = 0; i < keys.length; i++) {
                Object obj = (Object) map.get(keys[i]);
                if (null == obj) {
                    return "";
                } else if (obj instanceof String && i == keys.length - 1) {
                    return obj == null || obj.toString().equals("") ? defaluVal : obj.toString();
                } else if (obj instanceof Integer && i == keys.length - 1) {
                    return obj.toString();
                } else if (obj instanceof Long && i == keys.length - 1) {
                    return String.valueOf(obj);
                } else if (obj instanceof BigInteger && i == keys.length - 1) {
                    return ((BigInteger) obj).toString();
                } else if (obj instanceof Float && i == keys.length - 1) {
                    return Float.toString((Float) obj);
                } else if (obj instanceof Double && i == keys.length - 1) {
                    return Double.toString((Double) obj);
                } else if (obj instanceof BigDecimal && i == keys.length - 1) {
                    return obj.toString();
                } else if (obj instanceof List) {
                    return obj;
                } else if (obj instanceof HashMap && i == keys.length - 1 && keys[keys.length - 1].equals("v")) {
                    ArrayList list = new ArrayList();
                    list.add(obj);
                    return list;
                } else if (obj instanceof HashMap) {
//                        obj = com.alibaba.fastjson.JSONObject.toJSONString(obj);
//                        map = (HashMap) MapOprater.parseJSON2Map(obj.toString());
                    map = (HashMap) obj;
                } else {
//                    throw  new DefineException("7999", "MapOprater未使用的标签");
//                    map = (HashMap) MapOprater.parseJSON2Map(obj.toString());

                      return obj.toString();

                }

            }
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("873242", "解析有误，请检查上送报文格式");
        }
        return null;
    }


}
