package com.xs.serve;
import com.xs.config.Static;
import com.xs.exception.DefineException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


/**
 * @ClassName NIOServer
 * @Description TODO
 * @Company RBGT
 * @Version 1.0
 */
@Data
public class NioServer {


    private final static Logger log =  LoggerFactory.getLogger(NioServer.class);

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(10240);

    // 此标志是为不停服务，而关闭tcp服务设计的
    private boolean flag = true;

//    @PostConstruct
//    public void init(){
//
//        // 创建Selector并注册ServerSocketChannel
//        try {
//            selector = Selector.open();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        // 指定服务器端口
////        int port = 9999;
////        try {
////            System.out.println("端口:"+9999);
////            this.start(port);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//    }





    /**
     * 操作 - 执行创建一个ServerSocketChannel
     *
     * @param port 端口号
     * @return void
     * @Date 上午 09:34:04 2023年5月24日 0024
     **/
    public void start(int port) throws IOException {
        selector = Selector.open();
        // 打开通道
        serverChannel = ServerSocketChannel.open();
        // 创建ServerSocketChannel并设置为非阻塞模式
        serverChannel.configureBlocking(false);

        // 绑定端口
        serverChannel.socket().bind(new InetSocketAddress(port));


        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("启动监听端口： " + port);

        // 处理事件循环
        while (flag) {
            // 获取 - 等待事件发生
            selector.select();
            if(!flag){
                break;
            }

            String serid = UUID.randomUUID().toString().replace("-", "");
            MDC.put("serid", serid);

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            // 循环 - 处理链接上来的数据处理逻辑
            while (iterator.hasNext()) {
                // 获取 - 发生事件的SelectionKey
                SelectionKey key = iterator.next();
                iterator.remove();

                // 分支 - 此通道是否已准备好接受新的套接字连接
                if (key.isAcceptable()) {
                    // 设置 - 准备接口数据
                    handleAccept(key);
                } else if (key.isReadable()) {
                    // 操作 - 处理链接上报的数据
                    handleRead(key);
                }
            }
        }
    }

    /**
     * 操作 -注册迭代器，并将同道贴上可读数据标签
     *
     * @param key 发生事件的SelectionKey（每个链接标识）
     * @return void
     * @Date 上午 09:32:06 2023年5月24日 0024
     **/
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        log.info("新客户端连接地址: " + clientChannel.getRemoteAddress());
    }

    /**
     * 操作 - 读取链接上报的数据
     *
     * @param key 发生事件的SelectionKey（每个链接标识）
     * @return void
     * @Date 上午 09:32:55 2023年5月24日 0024
     **/
    private void handleRead(SelectionKey key) throws IOException {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            buffer.clear();

            int bytesRead = clientChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                byte[] lenData = new byte[6];
                buffer.get(lenData, 0, 6);
                String lenMessage = new String(lenData);

                log.info("接收报文长度: " + lenMessage);


                byte[] data = new byte[Integer.parseInt(lenMessage)];
                buffer.get(data,0, Integer.parseInt(lenMessage));
                String message = new String(data);
                log.info("接收报文内容:" + message);
                // 处理接收到的消息

                String response = "";
                try {
                    response = OKHTTPUtil.postFormStr(Static.accepthttpurl, "dc", message);
                } catch (DefineException e){
                    response = "000108"+ String.format("%8s",e.getCode()) + String.format("%100s",e.getMessage());
                }

                // 回复消息给客户端
//            String response = "Response from server";
                buffer.clear();
                buffer.put(response.getBytes());
                buffer.flip();
                clientChannel.write(buffer);

                clientChannel.close();
            } else if (bytesRead == -1) {
                log.info("客户端断开连接: " + clientChannel.getRemoteAddress());

                // 客户端断开连接
                clientChannel.close();
            }
        } catch (DefineException e){
            log.info(e.getMessage(),e);
        } catch (Exception e){
            log.info(e.getMessage(), e);
        }

    }


//    public void close(){
//        try {
////            this.flag = false;
//
////            this.serverChannel.socket().close();
////            serverChannel.socket().close();
////selector.wakeup();
//            System.out.println("ow1");
//            selector.close();
//            System.out.println("ow2");
//            serverChannel.close();
//            System.out.println("ow3");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
