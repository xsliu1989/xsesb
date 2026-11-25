package com.xs.cache;

import com.xs.datamodel.CustomerSysname;
import com.xs.datamodel.customer.CustomerRequestTag;
import com.xs.datamodel.provider.ProviderResponseTag;
import com.xs.model.Tag;
import com.xs.datamodel.customer.CustomerResponseTag;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import com.xs.datamodel.provider.ProviderRequestTag;
import com.xs.datamodel.provider.ProviderTransactionInfo;
import com.xs.db.baseservice.impl.CrudBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 本地缓存的处理类
 */
@Component
public class DataStructureCache {
    private final static Logger log =  LoggerFactory.getLogger(DataStructureCache.class);
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

    /** 源系统表info **/
    public static Map<String , CustomerTransactionInfo> customerTransactionInfoMap = new HashMap();
    /** 源系统表返回Tag **/
    public static Map<String, List<Tag>> customerRequestTagMap = new HashMap();
    /** 源系统表返回Tag **/
    public static Map<String, List<Tag>> customerResponseTagMap = new HashMap();
    /** 目标系统表info **/
    public static Map<String, ProviderTransactionInfo> providerTransactionInfoMap = new HashMap();
    /** 目标系统表请求Tag **/
    public static Map<String, List<Tag>> providerRequestTagMap = new HashMap();
    /** 目标系统表返回ag **/
    public static Map<String, List<Tag>> providerResponseTagMap = new HashMap();
    /** 每个渠道的连接数 **/
    public static Map<String, CustomerSysname>  customerSysnameMap = new HashMap();
    /** 渠道上送的唯一流水 */
    public static Map<String, Date> nonce = new HashMap<String, Date>();

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
            customerSysnameMap.put(cs.getSystype(), cs);
        }
    }


      public  void initCustomer(){
          List<CustomerTransactionInfo> customerTransactionInfoList = baseService.findAll(new CustomerTransactionInfo());
          for(CustomerTransactionInfo customerTransactionInfo : customerTransactionInfoList){

              String tranNo = customerTransactionInfo.getTranNo();
              customerTransactionInfoMap.put(tranNo, customerTransactionInfo);

              customerResponseTagMap.put(tranNo,initCustomerResponseTags(tranNo));

              customerRequestTagMap.put(tranNo,initCustomerRequestTags(tranNo));
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

              providerTransactionInfoMap.put(custNo, providerTransactionInfo);

              providerRequestTagMap.put(tranNo ,initProviderRequestTags(tranNo));

              providerResponseTagMap.put(tranNo ,initProviderResponseTags(tranNo));
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
