package com.kthowns.mobidic.storage.auth.repository.redis;

import com.kthowns.mobidic.domain.auth.repository.AuthRedisKey;
import com.kthowns.mobidic.domain.auth.repository.BlackListRepository;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BlackListRepositoryImpl implements BlackListRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void saveDeactivated(UUID userId, long ttlMillis) {
        String key = AuthRedisKey.DEACTIVATED + ":" + userId.toString();
        redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(ttlMillis));
    }

    @Override
    public boolean existsDeactivated(UUID userId) {
        try {
            String key = AuthRedisKey.DEACTIVATED + ":" + userId.toString();
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis connection failed during deactivated user check. Falling back to DB. userId: {}", userId, e);
            return userJpaRepository.existsByIdAndIsActiveFalse(userId);
        }
    }
}
