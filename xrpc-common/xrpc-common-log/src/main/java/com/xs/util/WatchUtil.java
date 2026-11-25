package com.xs.util;

import java.io.File;

import com.xs.cache.BizFlagCache;
import org.apache.commons.io.monitor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchUtil {



    private final static Logger log =  LoggerFactory.getLogger(WatchUtil.class);

    public static void watchFile(String filePath) {
        try {

            // 文件观察者
            FileAlterationObserver observer = new FileAlterationObserver(filePath);

            // 添加监听
            observer.addListener(new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    log.info(file.toString() + "文件修改了,将执行重新加载");
                    BizFlagCache.updata_biz_log_map();
                    log.info(file.toString() + "文件修改了,执行重新加载成功");
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
