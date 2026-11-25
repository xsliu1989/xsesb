package com.xs.admin.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用 JPA 访问层：admin 管理表与 com.xs.datamodel 元数据表共用。
 * 这样既复用既有实体，又避免为每张表重复编写 Repository。
 */
@Repository
public class GenericRepository {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(GenericRepository.class);


    @PersistenceContext
    private EntityManager em;

    public <T> T find(Class<T> entityClass, Object id) {
        return em.find(entityClass, id);
    }

    @Transactional
    public <T> T save(T entity) {
        return em.merge(entity);
    }

    @Transactional
    public void persist(Object entity) {
        em.persist(entity);
    }

    @Transactional
    public <T> void delete(Class<T> entityClass, Object id) {
        T entity = em.find(entityClass, id);
        if (entity != null) em.remove(entity);
    }

    /** 供 Tag 批量保存使用：直接删除已查询出的实体 */
    @Transactional
    public <T> void deleteRaw(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Transactional
    public void flush() {
        em.flush();
    }

    public <T> List<T> findBy(Class<T> entityClass, String field, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
        q.select(root).where(cb.equal(root.get(field), value));
        return em.createQuery(q).getResultList();
    }

    public <T> List<T> list(Class<T> entityClass, int page, int size, Map<String, Object> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
        q.select(root).where(buildPredicates(cb, root, filters));
        TypedQuery<T> tq = em.createQuery(q);
        tq.setFirstResult((page - 1) * size);
        tq.setMaxResults(size);
        return tq.getResultList();
    }

    public <T> long count(Class<T> entityClass, Map<String, Object> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<T> root = q.from(entityClass);
        q.select(cb.count(root)).where(buildPredicates(cb, root, filters));
        return em.createQuery(q).getSingleResult();
    }

    private <T> Predicate[] buildPredicates(CriteriaBuilder cb, Root<T> root, Map<String, Object> filters) {
        List<Predicate> ps = new ArrayList<>();
        if (filters != null) {
            filters.forEach((k, v) -> {
                if (v == null || "".equals(v) || "page".equals(k) || "size".equals(k)) return;
                if (v instanceof String) {
                    ps.add(cb.like(cb.lower(root.get(k).as(String.class)), "%" + ((String) v).toLowerCase() + "%"));
                } else {
                    ps.add(cb.equal(root.get(k), v));
                }
            });
        }
        return ps.toArray(new Predicate[0]);
    }
}
