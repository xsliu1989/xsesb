package com.xs.datamodel.provider;
import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProviderRequestTagRepository extends DaoRepository<ProviderRequestTag, Long>, JpaSpecificationExecutor<ProviderRequestTag> {
}
