package com.kthowns.mobidic.storage.dictionary.jparepository;

import com.kthowns.mobidic.domain.dictionary.model.WordDetail;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WordJpaRepositoryCustomImpl implements WordJpaRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned) {
        List<WordDetail> wordDetails = queryFactory
                .select(Projections.constructor(WordDetail.class,
                        QWord.word.id,
                        QWord.word.expression,
                        QWordStatistic.wordStatistic.difficulty.coalesce(0.5),
                        QWordStatistic.wordStatistic.accuracy.coalesce(0.0),
                        QWordStatistic.wordStatistic.isLearned.coalesce(false),
                        Expressions.constant(new ArrayList<DefinitionDto>()), // 빈 리스트
                        QWord.word.createdAt
                ))
                .from(QWord.word)
                .leftJoin(QWordStatistic.wordStatistic).on(QWordStatistic.wordStatistic.word.id.eq(QWord.word.id))
                .where(
                        QWord.word.vocabulary.id.eq(vocabularyId),
                        QWord.word.vocabulary.user.id.eq(userId),
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
                        QDefinition.definition.word.id,
                        QDefinition.definition.id,
                        QDefinition.definition.meaning,
                        QDefinition.definition.part
                )
                .from(QDefinition.definition)
                .where(QDefinition.definition.word.id.in(wordIds))
                .fetch();

        Map<UUID, List<DefinitionDto>> definitionMap = definitions.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(QDefinition.definition.word.id), // 단어 ID를 Key로
                        Collectors.mapping(tuple -> new DefinitionDto(
                                tuple.get(QDefinition.definition.id),
                                tuple.get(QDefinition.definition.meaning),
                                tuple.get(QDefinition.definition.part)
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
        return notLearned ? QWordStatistic.wordStatistic.isLearned.isFalse() : null;
    }
}
