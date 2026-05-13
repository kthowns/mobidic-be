package com.kthowns.mobidic.term.repository;

import com.kthowns.mobidic.term.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}
