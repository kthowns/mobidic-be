package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dto.WordDto;
import com.kimtaeyang.mobidic.entity.Rate;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.exception.ApiException;
import com.kimtaeyang.mobidic.repository.RateRepository;
import com.kimtaeyang.mobidic.repository.VocabRepository;
import com.kimtaeyang.mobidic.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;
    private final VocabRepository vocabRepository;
    private final RateRepository rateRepository;

    @Transactional
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabId)")
    public WordDto addWord(UUID vocabId, AddWordRequestDto request) {
        Vocab vocab = vocabRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(NO_VOCAB));

        int count = wordRepository.countByExpressionAndVocab(request.getExpression(), vocab);

        if (count > 0) {
            throw new ApiException(DUPLICATED_WORD);
        }

        Word word = Word.builder()
                .expression(request.getExpression())
                .vocab(vocab)
                .build();
        wordRepository.save(word);

        Rate rate = Rate.builder()
                .word(word)
                .correctCount(0)
                .incorrectCount(0)
                .isLearned(0)
                .build();
        rateRepository.save(rate);

        return WordDto.fromEntity(word);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vId)")
    public List<WordDto> getWordsByVocabId(UUID vId) {
        Vocab vocab = vocabRepository.findById(vId)
                .orElseThrow(() -> new ApiException(NO_VOCAB));

        return wordRepository.findByVocab(vocab)
                .stream().map(WordDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public WordDto updateWord(UUID wordId, AddWordRequestDto request) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(NO_WORD));

        long count = wordRepository.countByExpressionAndVocabAndIdNot(request.getExpression(), word.getVocab(), wordId);

        if (count > 0) {
            throw new ApiException(DUPLICATED_WORD);
        }

        word.setExpression(request.getExpression());
        wordRepository.save(word);

        return WordDto.fromEntity(word);
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public WordDto deleteWord(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(NO_WORD));

        wordRepository.delete(word);

        return WordDto.fromEntity(word);
    }
}
