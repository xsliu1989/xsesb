package com.xs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 
 * 
 * @author lxs
 */
@SpringBootApplication
public class XrpcModuleAcceptTtpDcApplication// extends SpringBootServletInitializer
{

    private final static Logger log =  LoggerFactory.getLogger(XrpcModuleAcceptTtpDcApplication.class);

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");
//        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");
//        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");
//        return builder.sources(XrpcModuleAcceptTtpDcApplication.class);
//    }

    public static void main(String[] args)
    {
        SpringApplication.run(XrpcModuleAcceptTtpDcApplication.class, args);
        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-accept-tcp-Dc 启动成功   (♥◠‿◠♥)   \n");

    }
}
