package com.xs.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 当前页参考：https://www.cnblogs.com/maguangyi/p/14215143.html
 * 若以后使用多数据源： https://www.cnblogs.com/cafebabe-yun/p/10679448.html
 */

@Data
@Configuration
public class DruidDataSourceConfig {




    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource(){

        return DruidDataSourceBuilder.create().build();
    }



//    /**
//     * 配置Druid后台监控功能
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean bean(){
//        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
//        //配置后台管理相关属性
//        Map<String,String> initParames = new HashMap<>();
//        initParames.put("loginUsername","admin");
//        initParames.put("loginPassword","123456");
//
//        //允许谁访问
//        initParames.put("allow","");
//        //禁止谁访问
//
//        bean.setInitParameters(initParames);
//        return bean;
//    }

    /**
     * 配置过滤器
     * @return
     */
//    @Bean
//    public FilterRegistrationBean filter(){
//        FilterRegistrationBean bean = new FilterRegistrationBean();
//        bean.setFilter(new WebStatFilter());
//
//        Map<String,String> initParams = new HashMap<>();
//        //这些请求不进行统计
//        initParams.put("exclusions", "*.js, *.css, /druid/*");
//        bean.setInitParameters(initParams);
//        return bean;
//    }




}
