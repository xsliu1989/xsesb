package com.xs.serve;

import com.xs.exception.DefineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Optional;

public class BeanXmlUtil {


    //日志log
    private final static Logger log =  LoggerFactory.getLogger(BeanXmlUtil.class);

    /**
     * 通用字符集
     */
    private static String charset;

    /**
     * Java对象转Xml公共
     *
     * @param source
     * @param sourceClass
     * @param encoding
     * @param isPretty
     * @param isNoFragment
     * @return
     * @throws JAXBException
     */
    private static byte[] beanToXml(Object source, Class<?> sourceClass, String encoding, boolean isPretty, boolean isNoFragment) throws DefineException {
        try {
            JAXBContext context = JAXBContext.newInstance(sourceClass);
            Marshaller marshaller = context.createMarshaller();
            // 是否格式化输出
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isPretty);
            // 设置字符编码
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            // 是否去掉默认XML头
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, isNoFragment);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(source, baos);
            return baos.toByteArray();
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }

    /**
     * Java对象转Xml字节数组
     *
     * @param source
     * @param sourceClass
     * @return
     * @throws JAXBException
     */
    public static byte[] beanToXmlBytes(Object source, Class sourceClass) throws DefineException {
        try {
            return BeanXmlUtil.beanToXml(source, sourceClass, charset, false, true);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }

    /**
     * Java对象转Xml字符串
     *
     * @param source
     * @param sourceClass
     * @return
     * @throws JAXBException
     */
    public static String beanToXmlStr(Object source, Class sourceClass) throws DefineException {
        try {
            Optional<byte[]> byteOpt = Optional.ofNullable(BeanXmlUtil.beanToXmlBytes(source, sourceClass));
            return new String(byteOpt.orElse(new byte[0]), Charset.forName(charset));
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }

    /**
     * Xml转Java对象
     *
     * @param xmlBytes
     * @param load
     * @return
     * @throws JAXBException
     * @throws UnsupportedEncodingException
     */
    public static Object xmlToBean(byte[] xmlBytes, Class<?> load) throws DefineException {
        try {
            JAXBContext context = JAXBContext.newInstance(load);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(new String(xmlBytes, Charset.forName(charset)));
            Object obj = unmarshaller.unmarshal(reader);
            return obj;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }

    public static Object xmlToBean(String xmlStr, Class<?> load) throws DefineException {
        try {
            JAXBContext context = JAXBContext.newInstance(load);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlStr);
            Object obj = unmarshaller.unmarshal(reader);
            return obj;
        }  catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DefineException("865","转换失败");
        }
    }
}
