package com.xs.client.impl;

import com.xs.cache.PortMap;
import com.xs.exception.DefineException;
import com.xs.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 双工通讯方式
 */
@Service(value = "sendC")
public class DefaultSocketClient implements SocketClient {

    private final static Logger log =  LoggerFactory.getLogger(DefaultSocketClient.class);
    private static int pos = 0;


    @Override
    public String forwordMess(String mess,String tranNo) throws DefineException, IOException {
        Socket socket = new Socket(PortMap.cPort_map.get(tranNo).getAddress(), PortMap.cPort_map.get(tranNo).getPort());

        OutputStream out = socket.getOutputStream();
        InputStream in = null;
        String serverMessage = null;
        try {
            byte[] buffer= mess.getBytes(PortMap.cPort_map.get(tranNo).getCharset());
            out.write(String.format("%0"+PortMap.cPort_map.get(tranNo).getFixedlength()+"d",buffer.length).getBytes());
            out.write(mess.getBytes(PortMap.cPort_map.get(tranNo).getCharset()));
            out.flush();
            if(in==null){
                in =socket.getInputStream();

            }else {
                if(in!=null){
                    in=socket.getInputStream();
                }
            }
            byte[] lenByteBuffer=new byte[PortMap.cPort_map.get(tranNo).getFixedlength()];
            int xlen=in.read(lenByteBuffer, 0, PortMap.cPort_map.get(tranNo).getFixedlength());

            if(xlen<0){
                throw new Exception("客户端异常没有接收到返回报文");
            }

            String	lengthTemplate=new String(lenByteBuffer);
            log.info("读取报文长度["+lengthTemplate+"]");

            int length=Integer.parseInt(lengthTemplate);
            buffer = new byte[length];

            int i=0;

            while(i<length){
                int result = in.read(buffer,i,length-i );
                if (result == -1) break;
                i +=result;
            }
            if(i!=length){

                throw new IOException("丢包异常");
            }
            serverMessage=new String(buffer,PortMap.cPort_map.get(tranNo).getCharset());
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
            log.info(e.getMessage(), e);
            throw new DefineException("8003", "连接异常");
        } finally {
            try {out.close();} catch (Exception e) {}
            try {in.close();} catch (Exception e) {}
            try {socket.close();} catch (Exception e) {}
        }

        return serverMessage;
    }


}
