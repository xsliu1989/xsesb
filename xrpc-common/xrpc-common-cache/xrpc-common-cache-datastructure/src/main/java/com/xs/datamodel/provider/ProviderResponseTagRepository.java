package com.xs.datamodel.provider;

import com.xs.datamodel.customer.CustomerResponseTag;
import com.xs.db.dao.DaoRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProviderResponseTagRepository extends DaoRepository<ProviderResponseTag, Long>, JpaSpecificationExecutor<ProviderResponseTag> {
}
