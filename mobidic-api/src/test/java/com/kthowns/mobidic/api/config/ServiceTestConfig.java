package com.kthowns.mobidic.api.config;

import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.word.jparepository.WordJpaRepository;
import com.kthowns.mobidic.domain.definition.service.DefinitionService;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.domain.word.service.WordService;
import com.kthowns.mobidic.storage.preset.jparepository.PresetVocabularyJpaRepository;
import com.kthowns.mobidic.domain.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.domain.quiz.service.CryptoService;
import com.kthowns.mobidic.api.security.jwt.JwtProperties;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.security.service.UserDetailsServiceImpl;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.statistic.util.DifficultyUtil;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class ServiceTestConfig {
    @Bean
    public DifficultyUtil difficultyUtil() {
        return new DifficultyUtil();
    }

    @Bean
    public PresetVocabularyJpaRepository presetVocabularyRepository() {
        return Mockito.mock(PresetVocabularyJpaRepository.class);
    }

    @Bean
    public WordJpaRepository wordRepository() {
        return Mockito.mock(WordJpaRepository.class);
    }

    @Bean
    public VocabularyJpaRepository vocabularyRepository() {
        return Mockito.mock(VocabularyJpaRepository.class);
    }

    @Bean
    public DefinitionJpaRepository definitionRepository() {
        return Mockito.mock(DefinitionJpaRepository.class);
    }

    @Bean
    public WordStatisticJpaRepository wordStatisticRepository() {
        return Mockito.mock(WordStatisticJpaRepository.class);
    }

    @Bean
    public UserJpaRepository userRepository() {
        return Mockito.mock(UserJpaRepository.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public CryptoService cryptoService() {
        return new CryptoService();
    }

    @Bean
    public WordService wordService() {
        return Mockito.mock(WordService.class);
    }

    @Bean
    public PresetVocabularyService presetVocabularyService() {
        return Mockito.mock(PresetVocabularyService.class);
    }

    @Bean
    public DefinitionService definitionService() {
        return Mockito.mock(DefinitionService.class);
    }

    @Bean
    public StatisticService statisticService() {
        return Mockito.mock(StatisticService.class);
    }

    @Bean
    public VocabularyService vocabularyService() {
        return Mockito.mock(VocabularyService.class);
    }

    @Bean
    public RedisTemplate redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }

    @Bean
    public ValueOperations valueOperations() {
        return Mockito.mock(ValueOperations.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 실제 컴포넌트 사용
    }

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public JwtProvider jwtProvider(
            JwtProperties jwtProperties,
            UserDetailsServiceImpl userDetailsServiceImpl
    ) {
        return new JwtProvider(jwtProperties, userDetailsServiceImpl);
    }

    @Bean
    public UserDetailsServiceImpl userDetailsServiceImpl(
            UserJpaRepository userJpaRepository
    ) {
        return new UserDetailsServiceImpl(userJpaRepository);
    }

    @Bean
    public JwtBlacklistService jwtBlacklistService() {
        return Mockito.mock(JwtBlacklistService.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}