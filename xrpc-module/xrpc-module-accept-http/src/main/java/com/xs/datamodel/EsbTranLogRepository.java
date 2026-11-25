package com.xs.datamodel;
import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EsbTranLogRepository extends DaoRepository<EsbTranLog, Long>, JpaSpecificationExecutor<EsbTranLog> {
}
