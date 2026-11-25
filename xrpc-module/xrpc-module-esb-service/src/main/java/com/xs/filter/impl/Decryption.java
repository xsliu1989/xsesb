package com.xs.filter.impl;

import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.filter.Filter;
import com.xs.serve.StructureUtil;
import com.xs.sm.sm4.SM4Utils;
import com.xs.dto.Dto;
import com.xs.util.RedisUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 解密工作
 *
 * 1. 生成一份公钥和私钥。
 * 2. 把公钥给渠道端。
 * 3. 渠道端每次请求时把用公钥加密的key发到服务端（调用initController）
 * 4. 服务端会用私钥把key解出来，保存到缓存里
 *
 * 5. 之后服务端的加解密用该key.
 *
 */
@Component
@Order(50)
public class Decryption implements Filter {
    @Override
    public void check(Dto dto) throws Exception {
        String tranNo = dto.getTranNo();
        String channel = dto.getChannel();
        CustomerTransactionInfo customerTransactionInfo = StructureUtil.getCusInfo(tranNo);
        // 需要对报文进行加解密操作
        if(null != customerTransactionInfo.getIsCryption() && customerTransactionInfo.getIsCryption().equals("true")){
            String key = RedisUtils.getHashKV("SIMKEY", channel);

            String data = dto.getData();
            data = SM4Utils.sm4Out_Base64(key, data);
            dto.setData(data);
        }
    }
}
