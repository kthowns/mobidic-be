package com.kthowns.mobidic.term.repository;

import com.kthowns.mobidic.term.entity.Term;
import com.kthowns.mobidic.term.type.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findFirstByTypeAndActiveTrueOrderByCreatedAtDesc(TermType type);

    Optional<Term> findByTypeAndVersion(TermType type, String version);

    List<Term> findByType(TermType type);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Term t" +
            " SET t.active = false" +
            " WHERE t.active = true" +
            " AND t.type = :type")
    void deactivateAllByType(@Param("type") TermType type);

    @Query("SELECT t.id FROM Term t" +
            " WHERE t.active = true" +
            " AND t.required = true")
    List<Long> findAllRequiredTermIds();

    long countByIdIn(Collection<Long> ids);

    List<Term> findByIdIn(Collection<Long> ids);

    boolean existsByTypeAndVersion(TermType type, String version);

    List<Term> findAllByActiveTrue();
}
