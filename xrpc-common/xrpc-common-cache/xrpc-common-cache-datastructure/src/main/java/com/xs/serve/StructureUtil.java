package com.xs.serve;

import com.xs.cache.DataStructureCache;
import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerRequestTag;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import com.xs.model.Tag;
import com.xs.util.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class StructureUtil {

    public static String cacheName;

    @Value("${cache.cacheName}")
    public void initName(String s){
        cacheName = s;
    }

    public static CustomerTransactionInfo getCusInfo(String tranNo){
        if(cacheName.equals("redis")) {
            return (CustomerTransactionInfo) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("cusinfo".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.customerTransactionInfoMap.get(tranNo);
        }
        return  null;
    }


    public static List<Tag> getCusReq(String tranNo){
        if(cacheName.equals("redis")) {
            return (List<Tag>) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("cusreq".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.customerRequestTagMap.get(tranNo);
        }
        return  null;
    }

    public static List<Tag> getCusRes(String tranNo){
        if(cacheName.equals("redis")) {
            return (List<Tag>) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("cusres".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.customerResponseTagMap.get(tranNo);
        }
        return  null;
    }


    public static ProviderTransactionInfo getProInfo(String tranNo){
        if(cacheName.equals("redis")) {
            return (ProviderTransactionInfo) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("proinfo".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.providerTransactionInfoMap.get(tranNo);
        }
        return  null;
    }

    public static List<Tag> getProReq(String tranNo){
        if(cacheName.equals("redis")) {
            return (List<Tag>) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("proreq".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.providerRequestTagMap.get(tranNo);
        }
        return  null;
    }

    public static List<Tag> getProRes(String tranNo){
        if(cacheName.equals("redis")) {
            return (List<Tag>) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("prores".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.providerResponseTagMap.get(tranNo);
        }
        return  null;
    }


    public static String getNonce(String nonce){
        if(cacheName.equals("redis")) {
            return RedisUtils.getKV(nonce);
        } else if(cacheName.equals("map")){
            if(DataStructureCache.nonce.get(nonce) == null){
                return null;
            } else {
                return DataStructureCache.nonce.get(nonce).toString();
            }

        }
        return  null;
    }

    public static void setNonce(String nonce){
        if(cacheName.equals("redis")) {
             RedisUtils.setKV(nonce, 5, nonce);
        } else if(cacheName.equals("map")){
             DataStructureCache.nonce.put(nonce, new Date());
        }
    }

    /**
     * 渠道连接请求设置的最大连接数（按渠道），超过次数后，则立即返回
     * @param systype
     * @return
     */
    public static CustomerSysname getConnectCountByChannel(String systype){
        if(cacheName.equals("redis")) {
            return (CustomerSysname) SerializeUtil.unserialize(RedisUtils.getHashKVForByte("customerSysname".getBytes(StandardCharsets.UTF_8), systype.getBytes(StandardCharsets.UTF_8)));
        } else if(cacheName.equals("map")){
            return DataStructureCache.customerSysnameMap.get(systype);
        }
        return  null;
    }



}
