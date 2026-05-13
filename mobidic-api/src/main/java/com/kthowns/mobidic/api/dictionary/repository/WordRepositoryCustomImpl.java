package com.kthowns.mobidic.api.dictionary.repository;

import com.kthowns.mobidic.api.dictionary.dto.DefinitionDto;
import com.kthowns.mobidic.api.dictionary.dto.WordDetail;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.kthowns.mobidic.api.dictionary.entity.QDefinition.definition;
import static com.kthowns.mobidic.api.dictionary.entity.QWord.word;
import static com.kthowns.mobidic.api.statistic.entity.QWordStatistic.wordStatistic;

@RequiredArgsConstructor
public class WordRepositoryCustomImpl implements WordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned) {
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
                .where(
                        word.vocabulary.id.eq(vocabularyId),
                        word.vocabulary.user.id.eq(userId),
                        isNotLearned(notLearned)
                )
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

    private BooleanExpression isNotLearned(boolean notLearned) {
        // true일 때만 '학습하지 않음' 조건 반환, false면 조건 없음(null)
        return notLearned ? wordStatistic.isLearned.isFalse() : null;
    }
}
