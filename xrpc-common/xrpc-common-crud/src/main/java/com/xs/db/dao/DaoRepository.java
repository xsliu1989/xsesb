package com.xs.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface DaoRepository<T, ID extends Serializable>
        extends  JpaRepository<T,ID> , JpaSpecificationExecutor<T> {

    boolean support(String modelType);

}
