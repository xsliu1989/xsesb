package com.xs.cache;

import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerRequestTag;
import com.xs.datamodel.customer.CustomerResponseTag;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderRequestTag;
import com.xs.datamodel.provider.ProviderResponseTag;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import com.xs.db.baseservice.impl.CrudBaseServiceImpl;
import com.xs.model.Tag;
import com.xs.serve.SerializeUtil;
import com.xs.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Redis 缓存结构加载器。仅在 cache.cacheName=redis 时启用。
 * 使用内存 map 缓存时若仍加载该类，会在启动时尝试连接 Redis 导致 Bean 创建失败。
 */
@Component
@ConditionalOnProperty(name = "cache.cacheName", havingValue = "redis")
public class DataStructureRedis {
    private final static Logger log =  LoggerFactory.getLogger(DataStructureRedis.class);
    @Autowired
    protected CrudBaseServiceImpl baseService;
//    @Autowired
//    private OtranInfoRepository otranInfoRepository;
//
//    @Autowired
//    private TtranInfoRepository ttranInfoRepository;
//
//    @Autowired
//    private TrequestTagRepository trequestTagRepository;
//
//    @Autowired
//    private OresponseTagRepository oresponseTagRepository;




//    /** 源系统表info **/
//    public static Map<String , CustomerTransactionInfo> customerTransactionInfoMap = new HashMap();
//    /** 源系统表返回Tag **/
//    public static Map<String, List<Tag>> customerRequestTagMap = new HashMap();
//    /** 源系统表返回Tag **/
//    public static Map<String, List<Tag>> customerResponseTagMap = new HashMap();
//    /** 目标系统表info **/
//    public static Map<String, ProviderTransactionInfo> providerTransactionInfoMap = new HashMap();
//    /** 目标系统表请求Tag **/
//    public static Map<String, List<Tag>> providerRequestTagMap = new HashMap();
//    /** 目标系统表返回ag **/
//    public static Map<String, List<Tag>> providerResponseTagMap = new HashMap();

    @PostConstruct
    public void init(){
        log.info("开始加载DataStructureCache");
        initCustomerChannel();
        initCustomer();
        initProvider();
        log.info("结束加载DataStructureCache");
    }

    public void initCustomerChannel(){

        List<CustomerSysname> customerSysnames = baseService.findAll(new CustomerSysname());
        for (CustomerSysname cs: customerSysnames) {
            RedisUtils.setHashKVForByte("customerSysname".getBytes(StandardCharsets.UTF_8),cs.getSystype().getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(cs) );

        }

    }

      public  void initCustomer(){
          List<CustomerTransactionInfo> customerTransactionInfoList = baseService.findAll(new CustomerTransactionInfo());
          for(CustomerTransactionInfo customerTransactionInfo : customerTransactionInfoList){

              String tranNo = customerTransactionInfo.getTranNo();

              RedisUtils.setHashKVForByte("cusinfo".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(customerTransactionInfo));

              RedisUtils.setHashKVForByte("cusreq".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(initCustomerRequestTags(tranNo)));

              RedisUtils.setHashKVForByte("cusres".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(initCustomerResponseTags(tranNo)));


              Object list =SerializeUtil.unserialize(RedisUtils.getHashKVForByte("cusres".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8)));


              //              customerTransactionInfoMap.put(tranNo, customerTransactionInfo);
//
//              customerResponseTagMap.put(tranNo,initCustomerResponseTags(tranNo));
//
//              customerRequestTagMap.put(tranNo,initCustomerRequestTags(tranNo));
          }
      }

      public List<Tag> initCustomerResponseTags(String tranNo){

          List<Tag> tags = new ArrayList<>();

          CustomerResponseTag customerResponseTag = new CustomerResponseTag();
          customerResponseTag.setCustomertranno("EQ_" + tranNo);
          List<CustomerResponseTag> customerResponseTagList = baseService.findAll(customerResponseTag);


          for(Tag customerResponseTag_ : customerResponseTagList){

              tags.add(customerResponseTag_);
          }
          tags.sort(Comparator.comparing(Tag::getSort));
          return tags;
      }

    public List<Tag> initCustomerRequestTags(String tranNo){

        List<Tag> tags = new ArrayList<>();

        CustomerRequestTag customerRequestTag = new CustomerRequestTag();
        customerRequestTag.setCustomerTranNo("EQ_" + tranNo);
        List<CustomerRequestTag> customerRequestList = baseService.findAll(customerRequestTag);

        for(CustomerRequestTag customerRequestList_ : customerRequestList){

            tags.add(customerRequestList_);
        }
        tags.sort(Comparator.comparing(Tag::getSort));
        return tags;
    }


      public void initProvider(){
          List<ProviderTransactionInfo> providerTransactionInfoList = baseService.findAll(new ProviderTransactionInfo());
          for(ProviderTransactionInfo providerTransactionInfo : providerTransactionInfoList){

              String custNo = providerTransactionInfo.getCustomerTranNo();
              String tranNo = providerTransactionInfo.getProviderTranNo();

              RedisUtils.setHashKVForByte("proinfo".getBytes(StandardCharsets.UTF_8), custNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(providerTransactionInfo));

              RedisUtils.setHashKVForByte("proreq".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(initProviderRequestTags(tranNo)));

              RedisUtils.setHashKVForByte("prores".getBytes(StandardCharsets.UTF_8), tranNo.getBytes(StandardCharsets.UTF_8), SerializeUtil.serialize(initProviderResponseTags(tranNo)));

//              providerTransactionInfoMap.put(tranNo, providerTransactionInfo);
//
//              providerRequestTagMap.put(tranNo ,initProviderRequestTags(tranNo));
//
//              providerResponseTagMap.put(tranNo ,initProviderResponseTags(tranNo));
          }

      }

      public List<Tag> initProviderRequestTags(String tranNo){
          List<Tag> tags = new ArrayList<>();

          ProviderRequestTag providerRequestTag = new ProviderRequestTag();
          providerRequestTag.setProviderTranNo("EQ_" + tranNo);

          List<ProviderRequestTag> providerRequestTagList = baseService.findAll(providerRequestTag);


          for(ProviderRequestTag providerRequestTag_ : providerRequestTagList){

              tags.add(providerRequestTag_);
          }

          tags.sort(Comparator.comparing(Tag::getSort));
          return tags;
      }


    public List<Tag> initProviderResponseTags(String tranNo){
        List<Tag> tags = new ArrayList<>();

        ProviderResponseTag providerResponseTag = new ProviderResponseTag();
        providerResponseTag.setProvidertranno("EQ_" + tranNo);

        List<ProviderResponseTag> providerResponseTagList = baseService.findAll(providerResponseTag);


        for(ProviderResponseTag providerResponseTag_ : providerResponseTagList){

            tags.add(providerResponseTag_);
        }
        tags.sort(Comparator.comparing(Tag::getSort));
        return tags;
    }



}
