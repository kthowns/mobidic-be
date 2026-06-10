package com.kthowns.mobidic.storage.term.jparepository;

import com.kthowns.mobidic.storage.term.jpaentity.UserAgreementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreementJpaRepository extends JpaRepository<UserAgreementJpaEntity, Long> {
}
