package com.kthowns.mobidic.storage.vocabulary.jparepository;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.storage.statistic.jpaentity.QWordStatisticJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.QVocabularyJpaEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class VocabularyDetailJpaRepositoryCustomImpl implements VocabularyDetailJpaRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QVocabularyJpaEntity vocabulary = QVocabularyJpaEntity.vocabularyJpaEntity;
    private final QWordStatisticJpaEntity wordStatistic = QWordStatisticJpaEntity.wordStatisticJpaEntity;

    @Override
    public List<VocabularyDetail> findVocabularyDetails(UUID userId) {
        return queryFactory
                .select(Projections.constructor(VocabularyDetail.class,
                        Projections.constructor(Vocabulary.class,
                                vocabulary.id,
                                vocabulary.user.id,
                                vocabulary.title,
                                vocabulary.description,
                                vocabulary.wordCount,
                                vocabulary.createdAt
                        ),
                        // 데이터가 없을 때를 대비해 coalesce(0.0) 추가
                        wordStatistic.isLearned.when(true).then(1.0).otherwise(0.0).avg().coalesce(0.0),

                        new CaseBuilder()
                                .when(wordStatistic.correctCount.add(wordStatistic.incorrectCount).gt(0)) // 문제를 한 번이라도 풀었나?
                                .then(
                                        wordStatistic.correctCount.doubleValue()
                                                .divide(wordStatistic.correctCount.add(wordStatistic.incorrectCount))
                                )
                                .otherwise(
                                        // 문제를 안 풀었을 때의 처리
                                        new CaseBuilder()
                                                .when(wordStatistic.correctCount.gt(0)).then(1.0)
                                                .otherwise(0.0)
                                )
                                .avg()
                                .coalesce(0.0)
                ))
                .from(vocabulary)
                .leftJoin(wordStatistic).on(wordStatistic.word.vocabulary.id.eq(vocabulary.id))
                .where(vocabulary.user.id.eq(userId))
                .groupBy(vocabulary.id, vocabulary.title, vocabulary.description, vocabulary.createdAt, vocabulary.wordCount)
                .fetch();
    }

    @Override
    public Optional<VocabularyDetail> findVocabularyDetail(UUID vocabularyId, UUID userId) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(VocabularyDetail.class,
                                Projections.constructor(Vocabulary.class,
                                        vocabulary.id,
                                        vocabulary.user.id,
                                        vocabulary.title,
                                        vocabulary.description,
                                        vocabulary.wordCount,
                                        vocabulary.createdAt
                                ),
                                // 데이터가 없을 때를 대비해 coalesce(0.0) 추가
                                wordStatistic.isLearned.when(true).then(1.0).otherwise(0.0).avg().coalesce(0.0),

                                new CaseBuilder()
                                        .when(wordStatistic.correctCount.add(wordStatistic.incorrectCount).gt(0)) // 문제를 한 번이라도 풀었나?
                                        .then(
                                                wordStatistic.correctCount.doubleValue()
                                                        .divide(wordStatistic.correctCount.add(wordStatistic.incorrectCount))
                                        )
                                        .otherwise(
                                                // 문제를 안 풀었을 때의 처리
                                                new CaseBuilder()
                                                        .when(wordStatistic.correctCount.gt(0)).then(1.0)
                                                        .otherwise(0.0)
                                        )
                                        .avg()
                                        .coalesce(0.0)
                        ))
                        .from(vocabulary)
                        .leftJoin(wordStatistic).on(wordStatistic.word.vocabulary.id.eq(vocabulary.id))
                        .where(
                                vocabulary.user.id.eq(userId),
                                vocabulary.id.eq(vocabularyId)
                        )
                        .groupBy(vocabulary.id, vocabulary.title, vocabulary.description, vocabulary.createdAt, vocabulary.wordCount)
                        .fetchOne());
    }
}
