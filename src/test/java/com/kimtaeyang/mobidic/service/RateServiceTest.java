package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.RateDto;
import com.kimtaeyang.mobidic.entity.Rate;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.entity.Word;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RateService.class, RateServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class RateServiceTest {
    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private VocabRepository vocabRepository;

    @Autowired
    private RateService rateService;

    @Test
    @DisplayName("[RateService] Get rate by word id success")
    void getRateByWordIdSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        Rate defaultRate = Rate.builder()
                .word(Mockito.mock(Word.class))
                .wordId(wordId)
                .correctCount(3)
                .incorrectCount(5)
                .isLearned(1)
                .build();

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(rateRepository.findRateByWord(any(Word.class)))
                .willReturn(Optional.of(defaultRate));

        //when
        RateDto response = rateService.getRateByWordId(UUID.randomUUID());

        //then
        assertEquals(defaultRate.getWordId(), response.getWordId());
        assertEquals(defaultRate.getIsLearned(), response.getIsLearned());
        assertEquals(defaultRate.getCorrectCount(), response.getCorrectCount());
        assertEquals(defaultRate.getIncorrectCount(), response.getIncorrectCount());
    }

    @Test
    @DisplayName("[RateService] Get vocab learning rate success")
    public void getVocabLearningRateSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();
        Double learningRate = 0.8;

        //given
        given(rateRepository.getVocabLearningRate(any(Vocab.class)))
                .willReturn(Optional.of(learningRate));
        given(vocabRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Vocab.class)));
        given(wordRepository.countByVocab(any(Vocab.class)))
                .willReturn(1L);

        //when
        Double foundLearningRate = rateService.getVocabLearningRate(vocabId);

        //then
        assertEquals(learningRate, foundLearningRate);
    }

    @Test
    @DisplayName("[RateService] toggle rate success")
    void toggleRateSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        Rate defaultRate = Rate.builder()
                .wordId(wordId)
                .word(Mockito.mock(Word.class))
                .correctCount(3)
                .isLearned(1)
                .incorrectCount(4)
                .build();

        ArgumentCaptor<Rate> captor =
                ArgumentCaptor.forClass(Rate.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(rateRepository.findRateByWord(any(Word.class)))
                .willReturn(Optional.of(defaultRate));
        given(rateRepository.save(any(Rate.class)))
                .willReturn(Mockito.mock(Rate.class));

        //when
        rateService.toggleRateByWordId(wordId);

        //then
        verify(rateRepository, times(1))
                .save(captor.capture());
        Rate savedRate = captor.getValue();

        assertEquals(defaultRate.getWordId(), savedRate.getWordId());
        assertEquals(0, savedRate.getIsLearned());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VocabRepository vocabRepository() { return Mockito.mock(VocabRepository.class); }

        @Bean
        public RateRepository rateRepository() {
            return Mockito.mock(RateRepository.class);
        }

        @Bean
        public WordRepository wordRepository() {
            return Mockito.mock(WordRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(rateRepository, wordRepository);
    }
}