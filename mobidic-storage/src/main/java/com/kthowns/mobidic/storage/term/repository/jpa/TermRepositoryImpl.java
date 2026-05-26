package com.kthowns.mobidic.storage.term.repository.jpa;

import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import com.kthowns.mobidic.storage.term.jpaentity.TermJpaEntity;
import com.kthowns.mobidic.storage.term.jparepository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepository {
    private final TermJpaRepository termJpaRepository;

    @Override
    public Optional<Term> readLatestByType(TermType type) {
        return termJpaRepository.findFirstByTypeAndActiveTrueOrderByCreatedAtDesc(type)
                .map(TermJpaEntity::toModel);
    }

    @Override
    public Optional<Term> readByTypeAndVersion(TermType type, String version) {
        return termJpaRepository.findByTypeAndVersion(type, version)
                .map(TermJpaEntity::toModel);
    }

    @Override
    public void deactivateAllByType(TermType type) {
        termJpaRepository.deactivateAllByType(type);
    }

    @Override
    public void append(Term term) {
        termJpaRepository.save(TermJpaEntity.fromModel(term));
    }

    @Override
    public List<Long> readAllRequiredTermIds() {
        return termJpaRepository.findAllRequiredTermIds();
    }

    @Override
    public long countByIds(List<Long> ids) {
        return termJpaRepository.countByIdIn(ids);
    }

    @Override
    public List<SimpleTerm> readActiveTerms() {
        return termJpaRepository.findAllByActiveTrue().stream()
                .map(TermJpaEntity::toSimpleModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTypeAndVersion(TermType type, String version) {
        return termJpaRepository.existsByTypeAndVersion(type, version);
    }
}
