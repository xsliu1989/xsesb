package com.xs.db.baseservice.impl;



import com.xs.db.baseservice.CrudBaseService;
import com.xs.db.dao.DaoRepository;
import com.xs.db.specification.SimpleSpecificationByBean;
import com.xs.exception.DefineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service("baseService")
public class CrudBaseServiceImpl<T, ID> implements CrudBaseService<T, ID> {


    private static final Logger logger = LoggerFactory.getLogger(CrudBaseService.class);

    @Autowired
    private List<DaoRepository> repositories;

    @Transactional
    @Override
    public int save(T t) throws DefineException {
        int i = 0;
        Object o = null;
        try {
            logger.info("save==className:" + t.getClass().getName());
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            o = baseRepository.save(t);
        } catch (DefineException de){
            throw de;
        } catch (Exception e) {
            throw this.getDefineEx(e);
        }
        if(o!=null) {
            i =1;
        }
        return i;
    }


    @Transactional
    @Override
    public int delete(T t) throws DefineException{
        int i = 1;

        try {
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            baseRepository.delete(t);
        }  catch (DefineException de){
            throw de;
        }  catch (Exception e) {
            i=0;
            throw this.getDefineEx(e);
        }
        return i;
    }

    @Override
    @Transactional()
    public int deleteBathList(List<T> list) {
        int i = 1;
        try {
            DaoRepository baseRepository = this.getRepository(list.get(0).getClass().getName());

            baseRepository.deleteInBatch(list);
        } catch (DefineException de){
            throw de;
        } catch (Exception e){
            i=0;
            throw this.getDefineEx(e);
        }

        return i;
    }

    @Override
    public int deleteEntitybyIds(T t1, T t2) {
        int i =1;
        try {
            DaoRepository baseRepository = this.getRepository(t1.getClass().getName());
            baseRepository.deleteById(t2);
        } catch (DefineException de){
            throw de;
        } catch (Exception e){
            i=0;
            throw this.getDefineEx(e);
        }
        return i;
    }




    @Transactional
    @Override
    public List findAll(T t) throws DefineException{
        try {
            SimpleSpecificationByBean simpleSpecification = new SimpleSpecificationByBean(t);
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            List<T> list =  baseRepository.findAll(simpleSpecification);
            return list;
        } catch (DefineException de){
            throw de;
        } catch (Exception e){
            throw this.getDefineEx(e);
        }
    }

    @Transactional
    @Override
    public List findAll(T t , Map<String,Object> map) throws DefineException{
        try {
            SimpleSpecificationByBean simpleSpecification = new SimpleSpecificationByBean(t, map);
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            List<T> list = baseRepository.findAll(simpleSpecification);
            return list;
        }   catch (DefineException de){
            throw de;
        } catch (Exception e){
            throw this.getDefineEx(e);
        }
    }

    @Transactional
    @Override
    public Page findAll(T t, Pageable pageable, Map<String,Object> map)  throws DefineException {

        try {
            SimpleSpecificationByBean simpleSpecification = new SimpleSpecificationByBean(t, map);
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            Page list = baseRepository.findAll(simpleSpecification, pageable);
            return list;
        }   catch (DefineException de){
            throw de;
        } catch (Exception e){
            throw this.getDefineEx(e);
        }
    }

    @Transactional
    @Override
    public List findAll(T t, Sort sort, Map<String,Object> map) throws DefineException{
        try {
            SimpleSpecificationByBean simpleSpecification = new SimpleSpecificationByBean(t, map);
            DaoRepository baseRepository = this.getRepository(t.getClass().getName());
            return baseRepository.findAll(simpleSpecification,sort);
        }   catch (DefineException de){
            throw de;
        } catch (Exception e){
            throw this.getDefineEx(e);
        }
    }

    public DefineException getDefineEx(Exception e){
        logger.info("操作异常，请联系管理员:" + e.getMessage(),e);
        logger.info(e.getMessage(),e);
//        Throwable t = e.getCause().getCause();
//        if(t instanceof SqlIntegrityConstraintViolationException){
//            SqlIntegrityConstraintViolationException sicv = (SqlIntegrityConstraintViolationException) t;
//            if( sicv.getSqlca().getSqlCode()== -801 || sicv.getSqlca().getSqlCode()== -803){
//                return new DefineException("001","主键冲突,  请多试几次！" + e.getMessage());
//            }
//        }
        return new DefineException("999", "操作异常，请联系管理员:" + e.getMessage());
    }

//    @Transactional
//    public List findAllChileOrg() throws Exception {
////        String orgcode = operatorUtil.getOperator().getOrganization().getOrgCode();
////        String parentcode = operatorUtil.getOperator().getOrganization().getOrgParentCode();
//        String orgid =  operatorUtil.getOperator().getOrganization().getOrdId()+"";
//        List chilelist = new ArrayList();
////        if(parentcode==null || "".equals(parentcode) || "0".equals(parentcode)){
////            chilelist = new ArrayList();
////        }else{
////            chilelist = organizationRepository.queryChileOrgid(orgcode);
////            chilelist.add(orgcode);
////        }
//        return chilelist;
//    }

    public DaoRepository getRepository(String name) throws DefineException{
        for (DaoRepository baseRepository : repositories) {

            if(baseRepository.support(name)){
                return baseRepository;
            }
        }
        return null;
    }


}
