package com.xs.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.xs.config.Static;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.serve.json.JSONObject;
import com.xs.sm.sm2.SM2Utils;
import com.xs.tcphttp.dto.CustomerReq;
import com.xs.tcphttp.dto.CustomerRes;
import com.xs.util.RedisUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;
import java.util.UUID;

@RestController
@Data
@RequestMapping("/initController")
public class InitController {

    private final static Logger log =  LoggerFactory.getLogger(InitController.class);

    /**
     * 在这里，渠道获得加解密key的接口
     *
     * @param json
     * @return
     */
    @PostMapping(value = "getcey")
    public String getcey(@RequestBody String json) {
        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);

        CustomerRes customerRes = new CustomerRes();


        try {
            JSONObject jsonObject = new JSONObject(json);
            String channel = jsonObject.get("channel").toString();
            String key = jsonObject.get("key").toString();


            /** 私钥制作 **/
            PrivateKey priK2 = SM2Utils.importPrivateKey("F:/sm2/private_key.pem");

            SM2 sm2obj = SmUtil.sm2();
            sm2obj.setPrivateKey(priK2);

            /** 用私钥把key解密 **/
            String decStr = StrUtil.utf8Str(sm2obj.decryptFromBcd(key, KeyType.PrivateKey));


            RedisUtils.setHashKV("SMKEY", channel, decStr );


            customerRes.setCode("0000");
            customerRes.setMessage("成功");

        } catch (DefineException de){
            log.info("处理异常", de);
        } catch (Exception e) {
            log.info("处理异常", e);
        }
        log.info("handel返回报文:"+customerRes.toString());
        return customerRes.toString();
    }
}
