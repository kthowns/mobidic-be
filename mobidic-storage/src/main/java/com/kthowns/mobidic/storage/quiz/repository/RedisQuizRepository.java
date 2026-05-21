package com.kthowns.mobidic.storage.quiz.repository;

import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisQuizRepository implements QuizRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveAnswer(String key, String answer, long expMillis) {
        redisTemplate.opsForValue().set(key, answer, Duration.ofMillis(expMillis));
    }

    @Override
    public String getAnswer(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteAnswer(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
