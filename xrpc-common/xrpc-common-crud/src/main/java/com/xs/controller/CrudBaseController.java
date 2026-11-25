package com.xs.controller;


import com.xs.controller.bean.CResult;
import com.xs.db.baseservice.impl.CrudBaseServiceImpl;
import com.xs.exception.DefineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import java.util.List;
import java.util.UUID;


/**
 * crud：   c增  u改  d删  r的意思是read读
 * 该类中只记录增改删公共方法
 */

public  class CrudBaseController<T> {

    private static final Logger logger = LoggerFactory.getLogger(CrudBaseController.class);

    @Autowired
    protected CrudBaseServiceImpl baseService;
//    @Autowired
//    protected ModelInfo modelInfo;

    //添加
    @PostMapping("/addEntity/{opntKey}")
    public CResult addEntity(@PathVariable String opntKey, @RequestBody @Valid final T entity, BindingResult bindingResult) {
        String serid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("serid", serid);
        logger.info("对象添加=====参数:"+entity.toString());
        if (bindingResult.hasErrors()){
            return new CResult(999, bindingResult.getFieldError().getDefaultMessage());
        }else{
            try {
                if(baseService.save(entity)==0){
                    return new CResult(999);
                }else{
                    return new CResult();
                }
            } catch (DefineException e) {
                return new CResult(Integer.parseInt(e.getCode()),e.getMessage());
            }


        }
    }



//    /**
//     *
//     * @param opntKey
//     * @param model
//     * @param entity
//     * @return
//     *
//     * 例子url : http://localhost:8888/role/deleteEntityById/22
//     * 参数： {"roleId":"99999998"}
//     */
//    //根据主键删除实体
//    @PostMapping("/{model}/deleteEntityById/{opntKey}")
//    public CResult deleteEntity(@PathVariable String opntKey, @PathVariable String model, @RequestBody final T entity){
//
//        try {
//            if(baseService.delete(entity)==0){
//                return new CResult(999);
//            }else{
//                return new CResult();
//            }
//        } catch (DefineException e) {
//            return new CResult(Integer.parseInt(e.getCode()),e.getMessage());
//        }
//    }
    /**
     * 批量删除 删除集合
     * @param objs
     * @return
     */
     @PostMapping("/deleteByBeans")
     public CResult deleteBathList(@RequestBody List<T> objs){
       try {
             if (baseService.deleteBathList(objs)==0){
                 return new CResult(999);
             } else{
                 return new CResult();
             }
         } catch (DefineException e){
             return new CResult(Integer.parseInt(e.getCode()),e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             return new CResult(999,e.getMessage());
         }

     }

//    /**
//     * 条件删除 批量删除
//     *
//     * @param model
//     * @return
//     */
//    @PostMapping("/{model}/deleteBySearch")
//    public CResult deleteBySearch(@PathVariable String model, @RequestBody final Map params) throws Exception{
//        Object obj = ClassEntity.newInstance(baseConfig.getModeandclassmap().get(model));
//        try {
//            List<T> list = baseService.findAll(obj, params);
//            baseService.deleteBathList(obj, list);
//            return new CResult();
//        } catch (DefineException e) {
//            return new CResult(Integer.parseInt(e.getCode()), e.getMessage());
//        }
//
//    }

//    /**
//     * 多主键删除
//     * @param list
//     * @param model
//     * @return
//     */
//    @PostMapping("/{model}/deleteEntitybyIds/{opntKey}")
//    public CResult deleteBathIds(@PathVariable String model, @PathVariable String opntKey, @RequestBody List<Object> list){
//            HashMap map1 = (HashMap) list.get(0);
//        int idCount = map1.size();
//        if (idCount>1){
//            Object obj1 = ClassEntity.newInstance(baseConfig.getModeandclassmap().get(model));
//            Object obj2 = ClassEntity.newInstance("com.xrpc.base.basedata.model.ids."+baseConfig.getIds().get(model));
//            int i = 0;
//            for (Object object : list) {
//                Map<String,Object> map = JSONObject.parseObject(JSON.toJSONString(object));
//                try {
//                    BeanUtils.populate(obj2,map);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    i = baseService.deleteEntitybyIds(obj1,obj2);
//                    if (i==0){
//                        return new CResult(999);
//                    }
//                } catch (DefineException e){
//                    return new CResult(Integer.parseInt(e.getCode()),e.getMessage());
//                }
//            }
//        } else {
//            Object obj = ClassEntity.newInstance(baseConfig.getModeandclassmap().get(model));
//             try {
//                 if (baseService.deleteBathList(obj,list)==0){
//                     return new CResult(999);
//                 } else{
//                     return new CResult();
//                 }
//             } catch (DefineException e){
//                 return new CResult(Integer.parseInt(e.getCode()),e.getMessage());
//             }
//        }
//
//        return new CResult();
//    }

    /**               以下为query                         **/

    //根据主键查询
    @PostMapping("/getEntityByBean")
    public CResult findEntityById( @RequestBody final T entity) throws Exception {
        return new CResult(baseService.findAll(entity));
    }

//
//    /**
//     * 分页查询
//     * @param model
//     * @param
//     * @return
//     * @throws Exception
//     *
//     * 例子url : "http://localhost:8888/promdsp/findAllBypageEntity/",
//     * 参数1    : {
//     * 	"pageSize":"1",            每页大小
//     * 	"pageNo":"0",              当前页
//     * 	"byFiled":"conTp",           排序
//     * 	"bySort":"DESC",
//     * 	"params":{"con1":"11"}       参数
//     *
//    参数2    {
//                "pageSize":"3",
//                "pageNo":"0",
//                "byFiled":"conTp,con1",
//                "bySort":"DESC",
//                "params":{"conTp":"LK_1","con1":"LK_2" }
//            }
//     *
//     *
//     *
//     * }
//     */
//
//    @PostMapping("/findAllBypageEntity/{model}")
//    public CResult findAllBypageEntity(@PathVariable String model, @RequestBody @Valid final CParams findAllByPageClass) throws Exception {
//        //        FindAllByPage findAllByPageClass = (FindAllByPage) entity;
//        // 分页相关
//        String pageSize =  findAllByPageClass.getPageSize() ;
//        String pageNo = findAllByPageClass.getPageNo();
//
//        // 排序相关
//        String byId = findAllByPageClass.getByFiled();
//        String byda = findAllByPageClass.getBySort();
//
//        // 请求实体的参数
//        Map params = findAllByPageClass.getParams();
//
//        logger.info("findAllBypageEntity==mode:" + model);
////        Object obj = modelInfo.handlerMap.get(model.toLowerCase()).newInstance();
////        Object obj = SpringContext.getBean(model);
////        Object obj = ClassEntity.newInstance(baseConfig.getModeandclassmap().get(model));
//
//
////        logger.info("findAllBypageEntity==obj:" + obj.toString());
//
//        try {
//            if ((pageSize == null || pageNo == null) && (byId == null || byda == null)) {                // 没有分页， 没有排序
//                List<T> list = baseService.findAll(obj,params);
//                return new CResult(list);
//
//
//            } else if ((pageSize != null || pageNo != null) && (byId == null || byda == null)) {       // 只有分页，没有排序
//
//                Pageable pageable = PageRequest.of(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
//                List<T> list = baseService.findAll(obj, pageable, params).getContent();
//                return new CResult(list,baseService.findAll(obj, pageable, params).getTotalElements());
//
//            } else if ((pageSize == null || pageNo == null) && (byId != null || byda != null)) {       // 没有分页，只有排序
//
//                Sort sort = getSort(byId, byda);
//                List<T> list = baseService.findAll(obj, sort, params);
//                return new CResult(list);
//
//            } else {                                                                                    // 有分页    有排序
//
//                Sort sort = getSort(byId, byda);
//                Pageable pageable = PageRequest.of(Integer.parseInt(pageNo), Integer.parseInt(pageSize), sort);
//                List<T> list = baseService.findAll(obj, pageable, params).getContent();
//
//                return new CResult(list,baseService.findAll(obj, pageable, params).getTotalElements());
//
//            }
//
//        } catch (DefineException e) {
//            return new CResult(Integer.parseInt(e.getCode()), e.getMessage());
//        }
//
//    }
//
//
//    /**
//     * 获取排序
//     * @param byId
//     * @param byda
//     * @return
//     */
//    public Sort getSort(String byId, String byda){
//        String[] byIds = StringUtils.splitPreserveAllTokens(byId,",");
//        String[] bydas = StringUtils.splitPreserveAllTokens(byda,",");
////        String[] byIds = StringHelper.commaToArray(byId,",");
////        String[] bydas = StringHelper.commaToArray(byda,",");
//        List<Sort.Order> orders = new ArrayList<Sort.Order>();
//        for(int i=0;i<byIds.length;i++){
//            Sort.Order order = null;
//
//            String byda_ = "";
//            try {               // 如果要排序的filed大于要排序的标识
//                byda_ = bydas[i];
//            } catch (Exception e){
//                byda_ = "AES";
//            }
//
//            if(byda_.toUpperCase().equals("ASC")){
//                order = new Sort.Order(Sort.Direction.ASC, byIds[i], Sort.NullHandling.NULLS_LAST);
//            }else{
//                order = new Sort.Order(Sort.Direction.DESC, byIds[i], Sort.NullHandling.NULLS_LAST);
//            }
//            orders.add(order);
//        }
//        Sort sort = Sort.by(orders);
//        return sort;
//    }

}
