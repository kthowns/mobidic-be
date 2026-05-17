package com.kthowns.mobidic.storage.term.jparepository;

import com.kthowns.mobidic.storage.term.jpaentity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}
