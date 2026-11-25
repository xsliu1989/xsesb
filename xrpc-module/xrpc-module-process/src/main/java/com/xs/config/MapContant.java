package com.xs.config;

/**
 * 系统常量
 * @author
 * @date 2019/01/30
 */
public class MapContant {

    /** 来自前端的请求，封装成map **/
    public static final String CUSTOMER_REQ_MAP            = "customer_req_map";
    /** 往前端返回的，封装成map **/
    public static final String CUSTOMER_RES_MAP         = "customer_res_map";
    /** 往第三方发送的请求， 封装成map **/
    public static final String PROVIDER_REQ_MAP         = "provider_req_map";
    /** 来自第三方的返回，封装成map **/
    public static final String PROVIDER_RES_MAP         = "provider_res_map";

    /** sendUrl 若实际URL与数据库里配的不一样 **/
    public static final String SEND_URL         = "send_url";

//    /** 目标系统表 **/
//    public static final String TTRANINFO         = "t_traninfo";
//
//    /** 源系统表 **/
//    public static final String OTRANINFO         = "o_traninfo";

//    /** sendData 发往第三方的数据，是否在发送之前修改 **/
//    public static final String SEND_DATA              ="send_data";
}
