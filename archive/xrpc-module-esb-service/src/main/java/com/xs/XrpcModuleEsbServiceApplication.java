package com.xs;

import com.xs.db.dao.MyJpaRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * process
 * 
 * @author lxs
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = MyJpaRepositoryFactoryBean.class)
public class XrpcModuleEsbServiceApplication extends SpringBootServletInitializer {

    private final static Logger log =  LoggerFactory.getLogger(XrpcModuleEsbServiceApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动ING...   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动ING...   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动ING...   (♥◠‿◠♥)   \n");
        return builder.sources(XrpcModuleEsbServiceApplication.class);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(XrpcModuleEsbServiceApplication.class, args);
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动成功   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动成功   (♥◠‿◠♥)   \n");
        log.info("(♥◠‿◠♥)  xrpc-module-process 启动成功   (♥◠‿◠♥)   \n");

    }
}
