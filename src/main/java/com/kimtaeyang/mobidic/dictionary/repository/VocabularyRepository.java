package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {
    List<Vocabulary> findByUser(User user);

    int countByTitleAndUserAndIdNot(String title, User user, UUID id);

    int countByTitleAndUser(String title, User user);

    Optional<Vocabulary> findByIdAndUser_Id(UUID id, UUID userId);
}