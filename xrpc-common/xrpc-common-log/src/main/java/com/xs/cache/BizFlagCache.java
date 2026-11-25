package com.xs.cache;


import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.xs.serve.json.JSONObject;
import java.io.File;

import static com.xs.util.WatchUtil.watchFile;

@Component
public class BizFlagCache {

    private final static Logger log =  LoggerFactory.getLogger(BizFlagCache.class);


    //往C发报文   c端有许多接口  所以把c端的地址端口信息与交易码绑定起来
    public static Map<String, Object> biz_log_map = new HashMap<>();


    public static String logconfigpath;

    @Value("${path.logconfigpath}")
    public void initName(String s){
        logconfigpath = s;
    }

    @PostConstruct
    public void init(){
        log.info("开始加载logConfig, 是否打印相关日志的配置");

        try {


            String parentPath = logconfigpath.substring(0, logconfigpath.lastIndexOf(File.separator));

            watchFile(parentPath);
            updata_biz_log_map();
            log.info("结束加载logConfig, 是否打印相关日志的配置，成功");

        } catch (Exception e) {
            log.info("结束加载logConfig, 是否打印相关日志的配置，不成功");
            log.info(e.getMessage(),e);
            e.printStackTrace();
        }


    }


    /**
     * 01\02等表示类型编号
     *
     * 样例：
     * {"01":"OK"
     *  "02":"OK"
     * }
     */
    public static void updata_biz_log_map() {
        try {

            log.info("加载"+ logconfigpath + "文件");
            FileInputStream fis = new FileInputStream(logconfigpath);
            String data = IOUtils.toString(fis, "UTF-8");
            log.info("获取"+ logconfigpath + "文件内容成功，并转化成String成功");
            JSONObject jsonObject = new JSONObject(data);
            biz_log_map = jsonObject.toMap();
            log.info("成功将logconfig转化成map,加载完成");
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
