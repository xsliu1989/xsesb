package com.xs.service;

import com.xs.cache.DataStructureCache;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import com.xs.dto.Dto;
import com.xs.exception.DefineException;
import com.xs.menu.LogType;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import com.xs.serve.OKHTTPUtil;
import com.xs.serve.SerializeUtil;
import com.xs.serve.StructureUtil;
import com.xs.util.MapConvert;
import com.xs.util.RedisUtils;
import com.xs.util.StringMapServe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public abstract  class DefaultAbstractServiceRedis implements Service<Dto> {

    private final static Logger log = (Logger) LoggerFactory.getLogger(DefaultAbstractServiceRedis.class);
    private static LogUtil logUtil;

    protected HashMap<String,Object> customer_req_map;
    protected HashMap<String,Object> provider_req_map;
    protected HashMap<String, Object> provider_res_map;
    protected HashMap<String, Object> customer_res_map;
    protected Dto dto;

    @Override
    public void execute(Dto dtoo) throws DefineException {
        try {
            this.dto = dtoo;
            logUtil.info(LogType.ZH_SERVE, log, "进入DefaultAbstractService转换主程序:" + dto.toString());

            // 取得消费者系统接口信息（含：请求接口号、请求类型（json/xml）、处理服务名、处理Filter服务名）
            CustomerTransactionInfo customerTransactionInfo = StructureUtil.getCusInfo(dto.getTranNo());
            // 取得源系统的请求TAG信息
            List<Tag> customerRequestTags = StructureUtil.getCusReq(dto.getTranNo());
            // 取得源系统的返回TAG信息
            List<Tag> customerResponseTags = StructureUtil.getCusRes(dto.getTranNo());



            // 取得消息提供者接口信息 (含：请求接口号、请求类型（json/xml）)
//            DataStructureCache.providerTransactionInfoMap.entrySet().stream().forEach((entry) -> {
//                ProviderTransactionInfo p = entry.getValue();
//                if (p.getCustomerTranNo().equals(dto.getTranNo())) {
//                    dto.setProviderTransactionInfo(entry.getValue());
//                }
//            });

            ProviderTransactionInfo providerTransactionInfo = StructureUtil.getProInfo(dto.getTranNo());
            dto.setProviderTransactionInfo(providerTransactionInfo);
            //        ProviderTransactionInfo providerTransactionInfo =DataStructureCache.providerTransactionInfoMap.get(dto.getTranNo());

            // 取得消息提供者系统的请求TAG信息
            List<Tag> providerRequestTags = StructureUtil.getProReq(providerTransactionInfo.getProviderTranNo());

            // 取得消息提供者系统的请求TAG信息
            List<Tag> providerResponseTags = StructureUtil.getProRes(providerTransactionInfo.getProviderTranNo());



            /** 前端上送的String类型dto.data()   ==>    HashMap*/
            HashMap<String, Object> request_map = StringMapServe.stringMap(dto.getChannelType(), dto.getData(), customerRequestTags);
            logUtil.info(LogType.ZH_SERVE, log, "》》》前端上送的String类型dto.data()   ==>    HashMap" + request_map.toString());

            /** requestHM  ==>  Customer_Req_MAP **/
            customer_req_map = new MapConvert().mapperMap(customerTransactionInfo.getTranNo(), request_map, customerRequestTags);
            update_customer_req_map();
            logUtil.info(LogType.ZH_SERVE, log, "requestHM  ==>  Customer_Req_MAP" + customer_req_map.toString());


            /** Customer_Req_MAP ==> Provider_Req_MAP **/
            provider_req_map = new MapConvert().mapperMap(providerTransactionInfo.getProviderTranNo(), customer_req_map, providerRequestTags);
            update_provider_req_map();
            logUtil.info(LogType.ZH_SERVE, log, "Customer_Req_MAP ==> Provider_Req_MAP" + provider_req_map.toString());


            /** provider_req_map ==> sendStr  **/
            String send_provider_Str = StringMapServe.mapString(providerTransactionInfo.getDataType(), provider_req_map, providerRequestTags);


            logUtil.info(LogType.F3REQ, log, send_provider_Str);

            update_sendQeury();
            String provider_return_Str = OKHTTPUtil.postFormStr(dto.getProvider_url() != null ? dto.getProvider_url() : providerTransactionInfo.getSendUrl(), providerTransactionInfo.getDataType(), providerTransactionInfo.getCodeType(), send_provider_Str);
            logUtil.info(LogType.F3RES, log, provider_return_Str);

            /** returnStr ==> response_map **/
            HashMap<String, Object> response_map = StringMapServe.stringMap(providerTransactionInfo.getDataType(), provider_return_Str, providerResponseTags);
            logUtil.info(LogType.ZH_SERVE, log, "返回报文转成MAP[returnStr ==> response_map]"+ response_map.toString());

            /** 组装返回的code和message **/
            dto.getMap().put("response_map", response_map);
            dto.getMap().put("providerResponseTags", providerResponseTags);
            code_message_to_dto();

            /** response_map  ==> provider_res_map **/
            provider_res_map = new MapConvert().mapperMap(dto.getTranNo(), response_map, providerResponseTags);
            update_provider_res_map();

            /** provider_res_map ==> customer_res_map **/
            customer_res_map = new MapConvert().mapperMap(dto.getTranNo(), provider_res_map, customerResponseTags);
            update_customer_res_map();

            /** customer_res_map ==> customer_return_String **/
            String customer_return_str = StringMapServe.mapString(dto.getChannelType(), customer_res_map, customerResponseTags);

            dto.setReturndata(customer_return_str);
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new DefineException("06987", "报文转化错误");
        }

    }


    public abstract void update_customer_req_map() ;
    public abstract void update_provider_req_map() ;
    public abstract void update_provider_res_map() ;
    public abstract void update_customer_res_map() ;
    public abstract void update_sendQeury();
    public abstract void code_message_to_dto();

//    public abstract boolean support(String trancode);
}




//