package com.kthowns.mobidic.api.preset.repository;

import com.kthowns.mobidic.api.preset.entity.PresetVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PresetVocabularyRepository extends JpaRepository<PresetVocabulary, UUID> {
}
