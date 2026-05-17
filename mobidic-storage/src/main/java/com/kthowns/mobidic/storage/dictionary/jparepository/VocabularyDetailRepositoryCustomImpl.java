package com.kthowns.mobidic.storage.dictionary.jparepository;

import com.kthowns.mobidic.api.dto.common.dictionary.VocabularyDetail;
import com.kthowns.mobidic.api.dto.common.dictionary.VocabularyDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.kthowns.mobidic.api.dictionary.entity.QVocabulary.vocabulary;
import static com.kthowns.mobidic.api.statistic.entity.QWordStatistic.wordStatistic;

@RequiredArgsConstructor
public class VocabularyDetailRepositoryCustomImpl implements VocabularyDetailRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<VocabularyDetail> findVocabularyDetails(UUID userId) {
        return queryFactory
                .select(Projections.constructor(VocabularyDetail.class,
                        Projections.constructor(VocabularyDto.class,
                                QVocabulary.vocabulary.id,
                                QVocabulary.vocabulary.title,
                                QVocabulary.vocabulary.description,
                                QVocabulary.vocabulary.wordCount,
                                QVocabulary.vocabulary.createdAt
                        ),
                        // 데이터가 없을 때를 대비해 coalesce(0.0) 추가
                        QWordStatistic.wordStatistic.isLearned.when(true).then(1.0).otherwise(0.0).avg().coalesce(0.0),

                        new CaseBuilder()
                                .when(QWordStatistic.wordStatistic.correctCount.add(QWordStatistic.wordStatistic.incorrectCount).gt(0)) // 문제를 한 번이라도 풀었나?
                                .then(
                                        QWordStatistic.wordStatistic.correctCount.doubleValue()
                                                .divide(QWordStatistic.wordStatistic.correctCount.add(QWordStatistic.wordStatistic.incorrectCount))
                                )
                                .otherwise(
                                        // 문제를 안 풀었을 때의 처리
                                        new CaseBuilder()
                                                .when(QWordStatistic.wordStatistic.correctCount.gt(0)).then(1.0)
                                                .otherwise(0.0)
                                )
                                .avg()
                                .coalesce(0.0)
                ))
                .from(QVocabulary.vocabulary) // 시작점을 vocabulary로 변경
                .leftJoin(QWordStatistic.wordStatistic).on(QWordStatistic.wordStatistic.word.vocabulary.id.eq(QVocabulary.vocabulary.id)) // Left Join
                .where(QVocabulary.vocabulary.user.id.eq(userId))
                .groupBy(QVocabulary.vocabulary.id, QVocabulary.vocabulary.title, QVocabulary.vocabulary.description, QVocabulary.vocabulary.createdAt, QVocabulary.vocabulary.wordCount)
                .fetch();
    }

    @Override
    public Optional<VocabularyDetail> findVocabularyDetail(UUID vocabularyId, UUID userId) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(VocabularyDetail.class,
                                Projections.constructor(VocabularyDto.class,
                                        QVocabulary.vocabulary.id,
                                        QVocabulary.vocabulary.title,
                                        QVocabulary.vocabulary.description,
                                        QVocabulary.vocabulary.wordCount,
                                        QVocabulary.vocabulary.createdAt
                                ),
                                // 데이터가 없을 때를 대비해 coalesce(0.0) 추가
                                QWordStatistic.wordStatistic.isLearned.when(true).then(1.0).otherwise(0.0).avg().coalesce(0.0),

                                new CaseBuilder()
                                        .when(QWordStatistic.wordStatistic.correctCount.add(QWordStatistic.wordStatistic.incorrectCount).gt(0)) // 문제를 한 번이라도 풀었나?
                                        .then(
                                                QWordStatistic.wordStatistic.correctCount.doubleValue()
                                                        .divide(QWordStatistic.wordStatistic.correctCount.add(QWordStatistic.wordStatistic.incorrectCount))
                                        )
                                        .otherwise(
                                                // 문제를 안 풀었을 때의 처리
                                                new CaseBuilder()
                                                        .when(QWordStatistic.wordStatistic.correctCount.gt(0)).then(1.0)
                                                        .otherwise(0.0)
                                        )
                                        .avg()
                                        .coalesce(0.0)
                        ))
                        .from(QVocabulary.vocabulary) // 시작점을 vocabulary로 변경
                        .leftJoin(QWordStatistic.wordStatistic).on(QWordStatistic.wordStatistic.word.vocabulary.id.eq(QVocabulary.vocabulary.id)) // Left Join
                        .where(
                                QVocabulary.vocabulary.user.id.eq(userId),
                                QVocabulary.vocabulary.id.eq(vocabularyId)
                        )
                        .groupBy(QVocabulary.vocabulary.id, QVocabulary.vocabulary.title, QVocabulary.vocabulary.description, QVocabulary.vocabulary.createdAt, QVocabulary.vocabulary.wordCount)
                        .fetchOne());
    }
}
