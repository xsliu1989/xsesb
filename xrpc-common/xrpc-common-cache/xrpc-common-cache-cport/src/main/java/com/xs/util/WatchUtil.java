package com.xs.util;

import java.io.IOException;

import com.xs.cache.PortMap;
import org.apache.commons.io.monitor.*;
import java.io.File;

public class WatchUtil {

    public static void watchFile(String filePath) {
        try {

//        // 也是只能写目录
//        String filePath = "C:\\Users\\Howl\\Desktop";

            // 文件观察者
            FileAlterationObserver observer = new FileAlterationObserver(filePath);

            // 添加监听
            observer.addListener(new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    System.out.println(file.toString() + "文件修改了");
                    PortMap.updata_cPort_map();
                }
            });

            // 监视器
            FileAlterationMonitor monitor = new FileAlterationMonitor(10);

            // 添加观察者
            monitor.addObserver(observer);

            // 启动线程
            monitor.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
