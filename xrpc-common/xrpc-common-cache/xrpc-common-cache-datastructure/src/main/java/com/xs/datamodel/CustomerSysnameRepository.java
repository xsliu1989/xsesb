package com.xs.datamodel;
import com.xs.datamodel.customer.CustomerRequestTag;
import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerSysnameRepository extends DaoRepository<CustomerSysname, Long>, JpaSpecificationExecutor<CustomerSysname> {
}
