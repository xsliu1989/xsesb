package com.xs.cache;

import com.xs.dto.CPort;
import com.xs.serve.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xs.util.WatchUtil.watchFile;

@Component
public class PortMap {
    //往C发报文   c端有许多接口  所以把c端的地址端口信息与交易码绑定起来
    public static Map<String, CPort> cPort_map = new HashMap<>();

    private final static Logger log =  LoggerFactory.getLogger(PortMap.class);

    public static String cportfilepath;

    @Value("${path.cportpath}")
    public void initName(String s){
        cportfilepath = s;
    }

    @PostConstruct
    public void init(){


        log.info("开始加载c_port");

        try {
            watchFile(cportfilepath);
            updata_cPort_map();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("结束加载c_port,成功加载");
    }


    /**
     * 样例：
     * {"cPort":[
     *        {
     * 		"trannos":["001", "002"],
     * 		"port":8888,
     * 		"charset":"utf8",
     * 		"address":"127.0.0.1",
     * 		"fixedlength":8
     *    },
     *    {
     * 		"trannos":["003", "004"],
     * 		"port":8887,
     * 		"charset":"utf8",
     * 		"address":"127.0.0.1",
     * 		"fixedlength":8
     *    }
     * ]}
     */
    public static void updata_cPort_map() {
        try {
            FileInputStream fis = new FileInputStream(cportfilepath);
            String data = IOUtils.toString(fis, "UTF-8");

            JSONObject jsonObject = new JSONObject(data);
            Map<String, Object> hashMap = jsonObject.toMap();
            ArrayList<HashMap> arrayList = (ArrayList) hashMap.get("cPort");

            for (HashMap hm:arrayList ) {
                ArrayList<String> arrayStr = (ArrayList<String>) hm.get("trannos");
                for (String str: arrayStr) {
                    CPort cPort = new CPort();
                    cPort.setTranno(str);
                    cPort.setPort((Integer) hm.get("port"));
                    cPort.setAddress((String)hm.get("address"));
                    cPort.setCharset((String)hm.get("charset"));
                    cPort.setFixedlength((Integer) hm.get("fixedlength"));
                    cPort.setType((String)hm.get("type"));
                    cPort_map.put(str, cPort);
                }
            }
            System.out.println(data);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
