package com.kthowns.mobidic.api.term.repository;

import com.kthowns.mobidic.api.term.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}
