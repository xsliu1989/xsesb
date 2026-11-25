package com.xs.util;


import com.xs.exception.DefineException;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import com.xs.serve.json.JSONObject;
import com.xs.serve.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * String转化成Map
 * String可以是JSON,XML,定长。
 */
public class StringMapServe {

    private final static Logger log = (Logger) LoggerFactory.getLogger(StringMapServe.class);

    private  static LogUtil logUtil;

    /**
     * 把String转成Map
     * @param type   支持的类型：JSON,XML,DC定长
     * @param data    String源
     * @param tranNo  该字段只有定长时需要。因为定长要转化成Map需要key,但是在定长的报文里是没有key值的，所以key值需要从库里取。
     * @return
     * @throws Exception
     */
    public static HashMap stringMap(String type, String data, List<Tag> tags) throws DefineException{
        try {
            if (type.equals("json") || type.equals("JSON")) { //确定前端传过来的数据格式是xml还是json
                JSONObject jsonObject = new JSONObject(data);
                return (HashMap) jsonObject.toMap();
            } else if (type.equals("xml") || type.equals("XML")) {
                try {
                    JSONObject jsonObject = XML.toJSONObject(data, true);
                    return (HashMap) jsonObject.toMap();
                } catch (Exception e) {
                    throw new DefineException("9999", "解析xml失败...");
                }

            } else if (type.equals("dc") || type.equals("DC")) {
                return DCStringMapUtil.dcStringMap(tags, data);
            }
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(),e);
            throw new DefineException("4675434","报文上送格式有误，请检查");
        }
        return  null;
    }


    /**
     * 把Map转成String
     * @param type
     * @param map
     * @return
     */
    public static String mapString(String type, HashMap map, List<Tag> tags){
        try {
            if (type.equals("json") || type.equals("JSON")) {
//            return JSON.toJSONString(map);
                return new JSONObject(map).toString();
            } else if (type.equals("xml") || type.equals("XML")) {
                return XML.toString(new JSONObject(map));
            } else if (type.equals("DC") || type.equals("dc")) {
                return DCMapStringUtil.dcMapString(tags, map);
            }
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(),e);
            throw new DefineException("4653434","报文上送格式有误，请检查");
        }

        return null;
    }



}
