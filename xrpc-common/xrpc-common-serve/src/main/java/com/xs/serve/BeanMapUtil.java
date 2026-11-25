package com.xs.serve;

import com.xs.exception.DefineException;
import org.apache.commons.beanutils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanMapUtil {

    //日志log
    private final static Logger log =  LoggerFactory.getLogger(BeanMapUtil.class);


    /**
     * Java转Map
     *
     * @param o
     * @return
     * @throws Exception
     */
    public static Map<String, String> objectToMap(Object o) throws DefineException {
        try {
            Map<String, String> map = new HashMap<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(o.getClass());
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(o) : "";
                map.put(key, String.valueOf(value));
            }
            return map;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("86555","转换失败");
        }
    }


    public static <T> T mapToBean(Map<String, Object> map, Class<?> clazz) throws DefineException {
        try {

            Object obj = clazz.newInstance();

            if (map != null && !map.isEmpty() && map.size() > 0) {

                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    String propertyName = entry.getKey(); // 属性名

                    Object value = entry.getValue(); // 属性值

                    String setMethodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

                    Field field = getClassField(clazz, propertyName); //获取和map的key匹配的属性名称

                    if (field == null) {

                        continue;

                    }

                    Class<?> fieldTypeClass = field.getType();

                    value = convertValType(value, fieldTypeClass);

                    try {

                        clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                        throw new DefineException("2347", "请检查格式");

                    }

                }

            }

            return (T) obj;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("8659","转换失败");
        }

    }

    /**

     * 根据给定对象类匹配对象中的特定字段

     * @param clazz

     * @param fieldName

     * @return

     */

    private static Field getClassField(Class<?> clazz, String fieldName) throws DefineException {
        try {

            if (Object.class.getName().equals(clazz.getName())) {

                return null;

            }

            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {

                if (field.getName().equals(fieldName)) {

                    return field;

                }

            }

            Class<?> superClass = clazz.getSuperclass(); //如果该类还有父类，将父类对象中的字段也取出

            if (superClass != null) { //递归获取

                return getClassField(superClass, fieldName);

            }

            return null;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("8651","转换失败");
        }

    }



    /**

     * 将map的value值转为实体类中字段类型匹配的方法

     * @param value

     * @param fieldTypeClass

     * @return

     */

    private static Object convertValType(Object value, Class<?> fieldTypeClass) throws DefineException{
        try {

            Object retVal = null;

            if (Long.class.getName().equals(fieldTypeClass.getName())

                    || long.class.getName().equals(fieldTypeClass.getName())) {

                retVal = Long.parseLong(value.toString());

            } else if (Integer.class.getName().equals(fieldTypeClass.getName())

                    || int.class.getName().equals(fieldTypeClass.getName())) {

                retVal = Integer.parseInt(value.toString());

            } else if (Float.class.getName().equals(fieldTypeClass.getName())

                    || float.class.getName().equals(fieldTypeClass.getName())) {

                retVal = Float.parseFloat(value.toString());

            } else if (Double.class.getName().equals(fieldTypeClass.getName())

                    || double.class.getName().equals(fieldTypeClass.getName())) {

                retVal = Double.parseDouble(value.toString());

            } else {

                retVal = value;

            }

            return retVal;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("8653","转换失败");
        }

    }
}
