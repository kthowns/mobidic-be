package com.kthowns.mobidic.storage.term.repository.jpa;

import com.kthowns.mobidic.domain.term.repository.UserAgreementRepository;
import com.kthowns.mobidic.storage.term.jpaentity.TermJpaEntity;
import com.kthowns.mobidic.storage.term.jpaentity.UserAgreementJpaEntity;
import com.kthowns.mobidic.storage.term.jparepository.TermJpaRepository;
import com.kthowns.mobidic.storage.term.jparepository.UserAgreementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAgreementRepositoryImpl implements UserAgreementRepository {
    private final UserAgreementJpaRepository userAgreementJpaRepository;
    private final TermJpaRepository termJpaRepository;

    @Override
    public void appendAgreements(UUID userId, List<Long> termIds) {
        List<TermJpaEntity> terms = termJpaRepository.findByIdIn(termIds);
        List<UserAgreementJpaEntity> userAgreements = terms.stream()
                .map(term ->
                        UserAgreementJpaEntity.create(userId, term)
                ).toList();

        userAgreementJpaRepository.saveAll(userAgreements);
    }
}
