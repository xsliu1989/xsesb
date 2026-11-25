package com.xs.service.impl;


import com.xs.context.SpringContext;
import com.xs.exception.DefineException;
import com.xs.client.SocketClient;
import com.xs.client.impl.SimplexSocketClient;
import com.xs.service.SendMessService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Data
@Service
public class
ToCService implements SendMessService {

    private final static Logger log = (Logger) LoggerFactory.getLogger(ToCService.class);


    private String sendStr =null;
    private String reciveXml = null;


    @Override
    public String sendMess(String mess,String tranNo) throws DefineException {
        log.info("开始ToZxyhService-sendMess");
        SocketClient socketClient = (SocketClient) SpringContext.getBean("sendC");
        try {//向C端发送
            reciveXml = socketClient.forwordMess(mess,tranNo);
            if(socketClient instanceof SimplexSocketClient && reciveXml ==null){
                // 走到这里说明是推送业务，没有返回
                // 单工形式

                return "";
            }
            log.info("接收返回报文:" + reciveXml);
        } catch (DefineException de){
            throw de;
        } catch (Exception e){
            throw new DefineException("1003", e.getMessage());
        } finally {

        }

        return reciveXml;

    }



}
