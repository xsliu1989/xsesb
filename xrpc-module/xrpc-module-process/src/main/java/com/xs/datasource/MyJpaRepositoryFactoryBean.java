package com.xs.datasource;

import com.xs.db.dao.impl.DaoRepositoryImpl;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class MyJpaRepositoryFactoryBean<R extends Repository<T, ID>, T, ID  extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public MyJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @SuppressWarnings("rawtypes")
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new MyJpaRepositoryFactory(em);
    }

    private static class MyJpaRepositoryFactory<T, ID  extends Serializable>
            extends JpaRepositoryFactory {

        private final EntityManager em;

        public MyJpaRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
        }

        @SuppressWarnings("unchecked")
        protected Object getTargetRepository(RepositoryMetadata metadata) {
            return new DaoRepositoryImpl<T, ID>(
                    (Class<T>) metadata.getDomainType(), em);
        }

        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return DaoRepositoryImpl.class;
        }
    }


}
