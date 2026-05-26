package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.domain.vocabulary.implementation.*;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {
    @InjectMocks
    private VocabularyService vocabularyService;

    @Mock
    private VocabularyValidator vocabularyValidator;

    @Mock
    private VocabularyAppender vocabularyAppender;

    @Mock
    private VocabularyReader vocabularyReader;

    @Mock
    private VocabularyUpdater vocabularyUpdater;

    @Mock
    private VocabularyRemover vocabularyRemover;

    private final UUID userId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();

    @Test
    @DisplayName("[VocabService] Add vocab success")
    void addVocabularySuccess() {
        // given
        String title = "title";
        String description = "description";
        Vocabulary vocabulary = new Vocabulary(vocabId, userId, title, description, 0L, null);

        given(vocabularyAppender.append(title, description, userId)).willReturn(vocabulary);

        // when
        Vocabulary response = vocabularyService.addVocabulary(userId, title, description);

        // then
        verify(vocabularyValidator).validateTitleAppendDuplication(title, userId);
        assertEquals(title, response.title());
        assertEquals(description, response.description());
        assertEquals(vocabId, response.id());
    }

    @Test
    @DisplayName("[VocabService] Get vocabs success")
    void getVocabularyDetailsSuccess() {
        // given
        List<VocabularyDetail> details = List.of(
                new VocabularyDetail(new Vocabulary(vocabId, userId, "title", "desc", 0L, null), 0.0, 0.0)
        );
        given(vocabularyReader.readDetailsByUserId(userId)).willReturn(details);

        // when
        List<VocabularyDetail> response = vocabularyService.getVocabularyDetails(userId);

        // then
        assertEquals(1, response.size());
        assertEquals("title", response.get(0).vocabulary().title());
    }

    @Test
    @DisplayName("[VocabService] Get vocab by id success")
    void getVocabularyByIdSuccess() {
        // given
        VocabularyDetail detail = new VocabularyDetail(new Vocabulary(vocabId, userId, "title", "desc", 0L, null), 0.0, 0.0);
        given(vocabularyReader.readDetailById(userId, vocabId)).willReturn(detail);

        // when
        VocabularyDetail response = vocabularyService.getVocabularyById(userId, vocabId);

        // then
        assertEquals(vocabId, response.vocabulary().id());
        assertEquals("title", response.vocabulary().title());
    }

    @Test
    @DisplayName("[VocabService] Update vocab success")
    void updateVocabularySuccess() {
        // given
        String title = "newTitle";
        String description = "newDesc";

        // when
        vocabularyService.updateVocabulary(userId, vocabId, title, description);

        // then
        verify(vocabularyValidator).validateTitleUpdateDuplication(title, vocabId, userId);
        verify(vocabularyUpdater).update(userId, vocabId, title, description);
    }

    @Test
    @DisplayName("[VocabService] Delete vocab success")
    void deleteVocabSuccess() {
        // when
        vocabularyService.deleteVocab(userId, vocabId);

        // then
        verify(vocabularyRemover).remove(vocabId, userId);
    }
}
