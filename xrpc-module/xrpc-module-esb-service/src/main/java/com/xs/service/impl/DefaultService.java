package com.xs.service.impl;

import com.xs.datamodel.provider.ProviderTransactionInfo;
import com.xs.exception.DefineException;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import com.xs.service.DefaultAbstractServiceRedis;
import com.xs.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service(value = "defaultService")
@Scope(value = "prototype")
public class DefaultService extends DefaultAbstractServiceRedis {


    private final static Logger log = (Logger) LoggerFactory.getLogger(DefaultService.class);

    private  static LogUtil logUtil;

    @Override
    public void update_customer_req_map() {

    }

    @Override
    public void update_provider_req_map() {
        // 往第三方发送时，头部信息可在此预先设置
        ProviderTransactionInfo providerTransactionInfo = (ProviderTransactionInfo) this.dto.getProviderTransactionInfo();
        this.provider_req_map.put("tranNo", providerTransactionInfo.getProviderTranNo());
    }

    @Override
    public void update_provider_res_map() {

    }

    @Override
    public void update_customer_res_map() {

    }

    @Override
    public void update_sendQeury() {

    }

    @Override
    public void code_message_to_dto() throws DefineException{
        try {
            HashMap<String, Object> response_map = (HashMap<String, Object>) dto.getMap().get("response_map");
            List<Tag> providerResponseTags = (List<Tag>) dto.getMap().get("providerResponseTags");

            Tag haveCodeTag = providerResponseTags.stream().filter(tag_ -> Objects.equals(tag_.getTagflag(), "code")).collect(Collectors.toList()).get(0);
            Tag haveMessageTag = providerResponseTags.stream().filter(tag_ -> Objects.equals(tag_.getTagflag(), "message")).collect(Collectors.toList()).get(0);
            String code = MapUtil.getVal(response_map, haveCodeTag.getTagMapping(), "").toString();
            String message = MapUtil.getVal(response_map, haveMessageTag.getTagMapping(), "").toString();
            dto.setCode(code);
            dto.setMessage(message);
        } catch (Exception e){
            log.error("报文异常，或者可能数据库没配置返回码标识; ", e);
            throw new DefineException("092", "异常，请检查是否配置返回码标识！");
        }
    }

//    @Override
//    public boolean support(String trancode) {
//        DataStructureCache.customerTransactionInfoMap.get(dto.getTranNo()).getClazz().equals("")
//    }
}
