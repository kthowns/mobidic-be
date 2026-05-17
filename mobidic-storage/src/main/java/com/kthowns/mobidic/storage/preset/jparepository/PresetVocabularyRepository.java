package com.kthowns.mobidic.storage.preset.jparepository;

import com.kthowns.mobidic.storage.preset.jpaentity.PresetVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PresetVocabularyRepository extends JpaRepository<PresetVocabulary, UUID> {
}
