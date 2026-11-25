package com.xs.db.dao.impl;

import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;


/**
 *
 * @param <T>
 * @param <ID>
 */
public class DaoRepositoryImpl<T, ID extends Serializable>
			extends SimpleJpaRepository<T, ID>
			implements DaoRepository<T, ID>
{



	private  Class<T> domainClass;


	public DaoRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.domainClass = entityInformation.getJavaType();
		System.out.println(this.domainClass);
	}

	public DaoRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.domainClass = domainClass;
	}


	@Override
	public boolean support(String modelType) {
		return domainClass.getName().toLowerCase().equals(modelType.toLowerCase());
	}

}
