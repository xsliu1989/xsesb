package com.xs.datamodel.customer;
import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerTransactionInfoRepository extends DaoRepository<CustomerTransactionInfo, Long>, JpaSpecificationExecutor<CustomerTransactionInfo> {

}
