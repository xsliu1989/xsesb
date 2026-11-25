package com.xs.db.specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**

 */
public class SimpleSpecificationByBean<T> implements Specification<T> {

    private T t ;


//    private Map<String,Object> params;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSpecificationByBean.class);

    public SimpleSpecificationByBean(T t){
        this.t = t;
    }

    public SimpleSpecificationByBean(T t, Map<String,Object> map){
        this.t = t;
//        this.params = map;
    }


    // 更规范：https://www.cnblogs.com/zouhong/p/11904142.html
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        try {
            List<Predicate> predicates = new ArrayList<>();

        for (Field field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            String fieldName     = field.getName();
            if(fieldName.equals("serialVersionUID")){
                continue;
            }

            Object filedValueObj = field.get(t);
//            if(this.params!=null){
//                filedValueObj = this.params.get(fieldName);
//            }

            if(filedValueObj == null || "".equals(filedValueObj)){
                continue;
            }

            String filedValue = filedValueObj.toString();
            String type  = filedValue.split("_").length == 1 ? "EQ" : filedValue.split("_")[0] ;
            String value =  filedValue.substring(filedValue.indexOf("_",0) + 1);
            if (value == null || value.equals("null") || value.equals("")){
                continue;
            }

            Class  clazz         = field.getType();


            switch (type) {
                case "EQ":
                    predicates.add(cb.equal(cb.trim(root.get(fieldName)).as(clazz), value));
                    break;
                case "LK":
                    predicates.add(cb.like(root.get(fieldName), "%" + value + "%"));
                    break;
                case "IN":
                    List list = Arrays.asList(value.split(","));
                    if (list != null && list.size() > 0) {
                        predicates.add(cb.trim(root.get(fieldName)).in(list));
                    }
                    break;
                case "BW":
                    predicates.add(cb.between(root.get(fieldName),value.split(",")[0],value.split(",")[1]));
                    break;
                case "GT":
                    predicates.add(cb.greaterThanOrEqualTo(root.get(fieldName),value));
                    break;
                case "LE":
                    predicates.add(cb.lessThanOrEqualTo(root.get(fieldName),value));
                    break;
//                case "GL":
//                   String[] strings = StringUtils.splitPreserveAllTokens(String.valueOf(value),",");
//                   predicates.add(cb.greaterThanOrEqualTo(root.get(fieldName),strings[0]));
//                   predicates.add(cb.lessThanOrEqualTo(root.get(fieldName),strings[1]));
//                    break;
                case "SUBSTR":
                    String[] vals = value.split(",");
                    predicates.add(cb.equal(cb.substring(root.get(fieldName),Integer.parseInt(vals[0]),Integer.parseInt(vals[1])), vals[2]));
                    break;
                case "NE":
                    predicates.add(cb.notEqual(cb.trim(root.get(fieldName)),  value));
                    break;

//                case "NI"://not in
//                    List list2 = new ArrayList();
//                    if (!value.equals("DROPVALUE")){
//                        list2 = Arrays.asList(StringUtils.split(value,","));
//                        if(list2!=null && list2.size()>0){
//                            predicates.add(cb.not(cb.trim(root.get(fieldName)).in(list2)));
//                        }
//                    }
//                    break;
                case "NAN": //NE + IS NULL
                    Predicate prd1 = cb.notEqual(cb.trim(root.get(fieldName)),  value);
                    Predicate prd2 = cb.isNull(root.get(fieldName));
                    predicates.add(cb.or(prd1,prd2));
                    break;
            }

        }
          Predicate p = cb.and(predicates.toArray(new Predicate[predicates.size()]));
          return p;
        } catch (Exception e) {
            logger.info(e.getMessage(),e);
        }
        return null;
    }

}
