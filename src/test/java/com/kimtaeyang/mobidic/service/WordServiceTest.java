package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dto.WordDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.entity.Rate;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.repository.DefRepository;
import com.kimtaeyang.mobidic.repository.RateRepository;
import com.kimtaeyang.mobidic.repository.VocabRepository;
import com.kimtaeyang.mobidic.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WordService.class, WordServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class WordServiceTest {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private VocabRepository vocabRepository;

    @Autowired
    private DefRepository defRepository;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private WordService wordService;

    @Test
    @DisplayName("[WordService] Add vocab success")
    void addWordSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("test")
                .build();

        ArgumentCaptor<Word> captor =
                ArgumentCaptor.forClass(Word.class);

        //given
        given(vocabRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Vocab.class)));
        given(wordRepository.countByExpressionAndVocab(anyString(), any(Vocab.class)))
                .willReturn(0);
        given(wordRepository.save(any(Word.class)))
                .willAnswer(invocation -> {
                    Word wordArg = invocation.getArgument(0);
                    wordArg.setId(wordId);
                    return wordArg;
                });

        //when
        WordDto response = wordService.addWord(UUID.randomUUID(), request);

        //then
        verify(wordRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getExpression(), response.getExpression());
        assertEquals(wordId, response.getId());
    }

    @Test
    @DisplayName("[WordService] Get words by vocab id success")
    void getWordsByVocabIdSuccess() {
        resetMock();

        Word defaultWord = Word.builder()
                .vocab(Mockito.mock(Vocab.class))
                .expression("expression")
                .build();

        Rate defaultRate = Rate.builder()
                .word(defaultWord)
                .isLearned(0)
                .incorrectCount(4)
                .correctCount(3)
                .build();

        ArrayList<Word> words = new ArrayList<>();
        words.add(defaultWord);

        //given
        given(vocabRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Vocab.class)));
        given(wordRepository.findByVocab(any(Vocab.class)))
                .willReturn(words);
        given(rateRepository.findRateByWord(any(Word.class)))
                .willReturn(Optional.of(defaultRate));

        //when
        List<WordDto> response = wordService.getWordsByVocabId(UUID.randomUUID());

        //then
        assertEquals(words.getFirst().getVocab().getId(), response.getFirst().getVocabId());
        assertEquals(words.getFirst().getExpression(), response.getFirst().getExpression());
    }

    @Test
    @DisplayName("[WordService] Update word success")
    void updateWordSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        Word defaultWord = Word.builder()
                .id(wordId)
                .vocab(Mockito.mock(Vocab.class))
                .expression("expression")
                .build();

        AddWordRequestDto request =
                AddWordRequestDto.builder()
                        .expression("expression2")
                        .build();

        ArgumentCaptor<Word> captor =
                ArgumentCaptor.forClass(Word.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultWord));
        given(vocabRepository.countByTitleAndMemberAndIdNot(anyString(), any(Member.class), any(UUID.class)))
                .willReturn(0);
        given(wordRepository.save(any(Word.class)))
                .willAnswer(invocation -> {
                    Word wordArg = invocation.getArgument(0);
                    wordArg.setExpression(request.getExpression());
                    return wordArg;
                });

        //when
        WordDto response =
                wordService.updateWord(wordId, request);

        //then
        verify(wordRepository, times(1))
                .save(captor.capture());
        assertEquals(wordId, response.getId());
        assertEquals(request.getExpression(), response.getExpression());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WordRepository wordRepository() {
            return Mockito.mock(WordRepository.class);
        }

        @Bean
        public VocabRepository vocabRepository() {
            return Mockito.mock(VocabRepository.class);
        }

        @Bean
        public DefRepository defRepository() {
            return Mockito.mock(DefRepository.class);
        }

        @Bean
        public RateRepository rateRepository() {
            return Mockito.mock(RateRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(wordRepository, vocabRepository, defRepository, rateRepository);
    }
}