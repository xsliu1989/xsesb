package com.xs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Static {

    public static String accepthttpurl;
    public static int acceptport;

    @Value("${url.xrpc-module-accept-http-dc}")
    public void initName(String s){
        accepthttpurl = s;
    }

    @Value("${url.xrpc-module-accept-tcp-dc}")
    public void initNameDk(int s){
        acceptport = s;
    }
}
