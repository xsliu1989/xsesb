package com.xs.service.impl;

import com.xs.datamodel.provider.ProviderTransactionInfo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service(value="updateUrlDefalutService")
@Scope(value = "prototype")
public class UpdateUrlDefalutService extends DefaultService {


    @Override
    public void update_sendQeury() {
        ProviderTransactionInfo providerTransactionInfo = (ProviderTransactionInfo) dto.getProviderTransactionInfo();
        dto.setProvider_url(providerTransactionInfo.getSendUrl()+ providerTransactionInfo.getProviderTranNo());
    }

    @Override
    public void update_customer_req_map(){
        customer_req_map.put("nonce", dto.getNonce());
    }


}
