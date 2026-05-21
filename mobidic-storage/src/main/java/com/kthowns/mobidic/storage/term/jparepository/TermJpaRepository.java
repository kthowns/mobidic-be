package com.kthowns.mobidic.storage.term.jparepository;

import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.storage.term.jpaentity.TermJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermJpaRepository extends JpaRepository<TermJpaEntity, Long> {
    Optional<TermJpaEntity> findFirstByTypeAndActiveTrueOrderByCreatedAtDesc(TermType type);

    Optional<TermJpaEntity> findByTypeAndVersion(TermType type, String version);

    List<TermJpaEntity> findByType(TermType type);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TermJpaEntity t" +
            " SET t.active = false" +
            " WHERE t.active = true" +
            " AND t.type = :type")
    void deactivateAllByType(@Param("type") TermType type);

    @Query("SELECT t.id FROM TermJpaEntity t" +
            " WHERE t.active = true" +
            " AND t.required = true")
    List<Long> findAllRequiredTermIds();

    long countByIdIn(Collection<Long> ids);

    List<TermJpaEntity> findByIdIn(Collection<Long> ids);

    boolean existsByTypeAndVersion(TermType type, String version);

    List<TermJpaEntity> findAllByActiveTrue();
}
