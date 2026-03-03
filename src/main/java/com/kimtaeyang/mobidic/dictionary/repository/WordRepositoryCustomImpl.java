package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDetail;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.kimtaeyang.mobidic.dictionary.entity.QDefinition.definition;
import static com.kimtaeyang.mobidic.dictionary.entity.QWord.word;
import static com.kimtaeyang.mobidic.statistic.entity.QWordStatistic.wordStatistic;

@RequiredArgsConstructor
public class WordRepositoryCustomImpl implements WordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId) {
        List<WordDetail> wordDetails = queryFactory
                .select(Projections.constructor(WordDetail.class,
                        word.id,
                        word.expression,
                        wordStatistic.difficulty.coalesce(0.5),
                        wordStatistic.accuracy.coalesce(0.0),
                        wordStatistic.isLearned.coalesce(false),
                        Expressions.constant(new ArrayList<DefinitionDto>()), // 빈 리스트
                        word.createdAt
                ))
                .from(word)
                .leftJoin(wordStatistic).on(wordStatistic.word.id.eq(word.id))
                .where(word.vocabulary.id.eq(vocabularyId))
                .where(word.vocabulary.user.id.eq(userId))
                .fetch();

        List<UUID> wordIds = wordDetails.stream()
                .map(WordDetail::id) // Record id가 String일 경우
                .toList();

        if (wordIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tuple> definitions = queryFactory
                .select(
                        definition.word.id,
                        definition.id,
                        definition.meaning,
                        definition.part
                )
                .from(definition)
                .where(definition.word.id.in(wordIds))
                .fetch();

        Map<UUID, List<DefinitionDto>> definitionMap = definitions.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(definition.word.id), // 단어 ID를 Key로
                        Collectors.mapping(tuple -> new DefinitionDto(
                                tuple.get(definition.id),
                                tuple.get(definition.meaning),
                                tuple.get(definition.part)
                        ), Collectors.toList())
                ));

        return wordDetails.stream()
                .map(wd -> new WordDetail(
                        wd.id(), wd.expression(), wd.difficulty(), wd.accuracy(),
                        wd.isLearned(),
                        definitionMap.getOrDefault(wd.id(), Collections.emptyList()),
                        wd.createdAt()
                ))
                .collect(Collectors.toList());
    }
}
