package com.kthowns.mobidic.storage.vocabulary.repository.jpa;

import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VocabularyRepositoryImpl implements VocabularyRepository {
    private final VocabularyJpaRepository vocabularyJpaRepository;
}
