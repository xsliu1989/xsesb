package com.xs.db.baseservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public interface CrudBaseService<T, ID>  {

    int save(T t);

    int delete(T t);

    int deleteBathList( List<T> list);

    int deleteEntitybyIds(T t1, T t2);

    List findAll(T t);

    List findAll(T t, Map<String, Object> map);


    Page findAll(T t, Pageable pageable, Map<String, Object> map);


    List findAll(T t, Sort sort, Map<String, Object> map);

}
