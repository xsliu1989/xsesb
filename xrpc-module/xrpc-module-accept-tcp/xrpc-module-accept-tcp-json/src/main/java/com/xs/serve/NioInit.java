package com.xs.serve;

import com.xs.config.Static;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class NioInit{
//    @Autowired
//    NioServer nioServer;
    Thread serverThread;
    NioServer nioServer;

    @PostConstruct
    public void run() {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nioServer = new NioServer();
                    nioServer.start(Static.acceptport);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "JSON Serve"); // 线程的名字
        serverThread.start();
    }

//    public void closed(){
//        nioServer.setFlag(false);
//
//        nioServer.close();
////        serverThread.interrupt();
//    }
}
