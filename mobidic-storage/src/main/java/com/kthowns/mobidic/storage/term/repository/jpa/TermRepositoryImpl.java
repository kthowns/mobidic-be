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
                .map(this::mapToModel);
    }

    @Override
    public Optional<Term> readByTypeAndVersion(TermType type, String version) {
        return termJpaRepository.findByTypeAndVersion(type, version)
                .map(this::mapToModel);
    }

    @Override
    public void deactivateAllByType(TermType type) {
        termJpaRepository.deactivateAllByType(type);
    }

    @Override
    public void append(Term term) {
        TermJpaEntity entity = TermJpaEntity.builder()
                .type(term.getType())
                .version(term.getVersion())
                .required(term.isRequired())
                .content(term.getContent())
                .build();
        termJpaRepository.save(entity);
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
                .map(entity -> SimpleTerm.builder()
                        .id(entity.getId())
                        .type(entity.getType())
                        .version(entity.getVersion())
                        .required(entity.isRequired())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTypeAndVersion(TermType type, String version) {
        return termJpaRepository.existsByTypeAndVersion(type, version);
    }

    private Term mapToModel(TermJpaEntity entity) {
        return Term.builder()
                .id(entity.getId())
                .type(entity.getType())
                .version(entity.getVersion())
                .required(entity.isRequired())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
