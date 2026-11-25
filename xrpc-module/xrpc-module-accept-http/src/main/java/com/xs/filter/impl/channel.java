package com.xs.filter.impl;

import com.xs.config.BinFaBean;
import com.xs.config.Static;
import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.exception.DefineException;
import com.xs.filter.Filter;
import com.xs.serve.StructureUtil;
import com.xs.tcphttp.dto.CustomerReq;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
这个是为了控制渠道端的，请求并发数。当并发数超过了设置的大小，就直接挡回去。

 1. 如果是管理员直接关闭对应渠道端的访问权限。    -- 1.需要管理员把相关配置到数据库中; 2.需要把数据同步到缓存区；3.修改后需要同步到缓存区
 2. 如果是渠道端的访问超过了并发数，将控制渠道端请求。-- 1. 需要管理员把并发数配置在数据库中 2.需要把数据同步到缓存区；3.修改后需要同步到缓存区；   4.需要把当前渠道的并发数实时更新到缓存区（考虑到返回异常和返回报错时，或者正常返回时，或者在过滤器端返回异常，都需要再从缓存区清除）
 3. 如果是对应接口超过了并发数，将控制当前请求。    -- 1. 需要管理员把并发数配置在数据库中 2.需要把数据同步到缓存区；3.修改后需要同步到缓存区；   4.需要把当前渠道的并发数实时更新到缓存区（考虑到返回异常和返回报错时，或者正常返回时，或者在过滤器端返回异常，都需要再从缓存区清除）

 *
 */
@Component
@Order(1)
public class channel implements Filter {
    @Override
    public void check(CustomerReq dto) throws DefineException {
        // 渠道请求，这里有接口的并发量
        CustomerTransactionInfo customerTransactionInfo = StructureUtil.getCusInfo(dto.getTranNo());
        // 请求渠道信息，这里有渠道的请求并发量
        CustomerSysname customerSysname = StructureUtil.getConnectCountByChannel(dto.getChannel());

        // 准备检查并发   1. 按渠道检查并发

        String nonce = dto.getNonce();

        BinFaBean binFaBean =  new BinFaBean(nonce, dto.getChannel(), dto.getTranNo(), new Date());


        Static.nonce.add(binFaBean);

        // 当前渠道的并发请求
        int channelSize = Static.nonce.stream().filter(bf -> bf.getChannel().equals(dto.getChannel())).collect(Collectors.toList()).size();

        // 当前接口的请求
        int trannoSize = Static.nonce.stream().filter(bf -> bf.getTranno().equals(dto.getTranNo())).collect(Collectors.toList()).size();


        if(channelSize > Integer.parseInt(customerSysname.getSize())){
            throw new DefineException("9433", "已达到渠道请求并发量");
        }

        if(trannoSize > Integer.parseInt(customerTransactionInfo.getSize())){
            throw new DefineException("2132", "已达到接口请求并发量");
        }


    }
}
