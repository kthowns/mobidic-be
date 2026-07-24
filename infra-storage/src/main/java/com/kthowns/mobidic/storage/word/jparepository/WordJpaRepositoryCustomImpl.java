package com.kthowns.mobidic.storage.word.jparepository;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.storage.definition.jpaentity.QDefinitionJpaEntity;
import com.kthowns.mobidic.storage.statistic.jpaentity.QWordStatisticJpaEntity;
import com.kthowns.mobidic.storage.word.jpaentity.QWordJpaEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WordJpaRepositoryCustomImpl implements WordJpaRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QWordJpaEntity word = QWordJpaEntity.wordJpaEntity;
    private final QWordStatisticJpaEntity wordStatistic = QWordStatisticJpaEntity.wordStatisticJpaEntity;
    private final QDefinitionJpaEntity definition = QDefinitionJpaEntity.definitionJpaEntity;

    @Override
    public List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned) {
        List<WordDetail> wordDetails = queryFactory
                .select(Projections.constructor(WordDetail.class,
                        word.id,
                        word.expression,
                        wordStatistic.difficulty.coalesce(0.5),
                        wordStatistic.accuracy.coalesce(0.0),
                        wordStatistic.isLearned.coalesce(false),
                        Expressions.constant(new ArrayList<Definition>()), // 빈 리스트 (도메인 모델 Definition 사용)
                        word.createdAt
                ))
                .from(word)
                .leftJoin(wordStatistic).on(wordStatistic.wordId.eq(word.id))
                .where(
                        word.vocabulary.id.eq(vocabularyId),
                        word.vocabulary.userId.eq(userId),
                        isNotLearned(notLearned)
                )
                .fetch();

        List<UUID> wordIds = wordDetails.stream()
                .map(WordDetail::id)
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

        Map<UUID, List<Definition>> definitionMap = definitions.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(definition.word.id), // 단어 ID를 Key로
                        Collectors.mapping(tuple -> Definition.of(
                                tuple.get(definition.id),
                                tuple.get(definition.word.id),
                                tuple.get(definition.meaning),
                                tuple.get(definition.part),
                                tuple.get(definition.createdAt),
                                tuple.get(definition.updatedAt)
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
