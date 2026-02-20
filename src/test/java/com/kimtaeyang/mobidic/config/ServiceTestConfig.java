package com.kimtaeyang.mobidic.config;

import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.dictionary.repository.DefinitionRepository;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import com.kimtaeyang.mobidic.dictionary.service.VocabularyService;
import com.kimtaeyang.mobidic.dictionary.service.WordService;
import com.kimtaeyang.mobidic.quiz.service.CryptoService;
import com.kimtaeyang.mobidic.security.jwt.JwtBlacklistService;
import com.kimtaeyang.mobidic.security.jwt.JwtProperties;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.security.service.UserDetailsServiceImpl;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
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
    public WordRepository wordRepository() {
        return Mockito.mock(WordRepository.class);
    }

    @Bean
    public VocabularyRepository vocabularyRepository() {
        return Mockito.mock(VocabularyRepository.class);
    }

    @Bean
    public DefinitionRepository definitionRepository() {
        return Mockito.mock(DefinitionRepository.class);
    }

    @Bean
    public WordStatisticRepository wordStatisticRepository() {
        return Mockito.mock(WordStatisticRepository.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
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
            UserRepository userRepository
    ) {
        return new UserDetailsServiceImpl(userRepository);
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