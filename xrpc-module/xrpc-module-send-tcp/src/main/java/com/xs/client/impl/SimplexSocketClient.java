package com.xs.client.impl;

import com.xs.cache.PortMap;
import com.xs.exception.DefineException;
import com.xs.client.SocketClient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 单工通讯方式
 *
 *
 */
@Data
public class SimplexSocketClient implements SocketClient {

    private final static Logger log =  LoggerFactory.getLogger(SimplexSocketClient.class);



    @Override
    public String forwordMess(String mess,String tranNo) throws DefineException, IOException {
        Socket socket = new Socket(PortMap.cPort_map.get(tranNo).getAddress(), PortMap.cPort_map.get(tranNo).getPort());
        OutputStream out = out = socket.getOutputStream();
//        InputStream in = null;
        String serverMessage = null;
        try {
             // 连接
            // 发送请求

            byte[] buffer= mess.getBytes(PortMap.cPort_map.get(tranNo).getCharset());
            out.write(String.format("%0"+PortMap.cPort_map.get(tranNo).getFixedlength()+"d",buffer.length).getBytes());
            out.write(mess.getBytes(PortMap.cPort_map.get(tranNo).getCharset()));
            out.flush();

            log.info("向后台推送成功！！！");

//            serverMessage="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                                "<transtrem>" +
//                                "   <header> " +
//                                "   </header>" +
//                                "   <body>" +
//                                "      <response>\r\n" +
//                                "        <returnCode>000000</returnCode>" +
//                                "        <returnMess>推送成功</returnMess>" +
//                                "      </response>" +
//                                "   </body>" +
//                                "</transtrem>";
        } catch (UnknownHostException e) {
           // e.printStackTrace();
            log.info("连接异常，请确认端口", e);
            throw new DefineException("8001", "连接异常，请确认端口");
        } catch (java.net.ConnectException ce){
          //  ce.printStackTrace();
            log.info("连接拒绝，请确认对方服务是否启动", ce);
            throw new DefineException("8004", "连接拒绝，请确认对方服务是否启动");
        }catch (IOException e) {
           // e.printStackTrace();
            log.info("连接异常，IO异常", e);
            throw new DefineException("8002", "连接异常，IO异常");
        } catch (Exception e) {
          //  e.printStackTrace();
            log.info("连接异常", e);
            throw new DefineException("8003", "连接异常");
        } finally {
            try {out.close();} catch (Exception e) {}
            try {socket.close();} catch (Exception e) {}

        }

        return serverMessage;
    }


}
