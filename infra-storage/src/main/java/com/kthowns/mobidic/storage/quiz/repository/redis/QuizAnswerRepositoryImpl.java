package com.kthowns.mobidic.storage.quiz.repository.redis;

import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuizAnswerRepositoryImpl implements QuizAnswerRepository {
    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Override
    public void append(String key, QuizAnswer quizAnswer, long expMillis) {
        objectRedisTemplate.opsForValue().set(key, quizAnswer, Duration.ofMillis(expMillis));
    }

    @Override
    public Optional<QuizAnswer> read(String key) {
        return Optional.ofNullable((QuizAnswer) objectRedisTemplate.opsForValue().get(key));
    }

    @Override
    public void remove(String key) {
        objectRedisTemplate.delete(key);
    }
}
