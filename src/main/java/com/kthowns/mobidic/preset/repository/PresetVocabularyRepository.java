package com.kthowns.mobidic.preset.repository;

import com.kthowns.mobidic.preset.entity.PresetVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PresetVocabularyRepository extends JpaRepository<PresetVocabulary, UUID> {
}
