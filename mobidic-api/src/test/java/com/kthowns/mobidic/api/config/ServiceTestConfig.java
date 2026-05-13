package com.kthowns.mobidic.api.config;

import com.kthowns.mobidic.api.auth.service.AuthService;
import com.kthowns.mobidic.api.dictionary.repository.DefinitionRepository;
import com.kthowns.mobidic.api.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.api.dictionary.repository.WordRepository;
import com.kthowns.mobidic.api.dictionary.service.DefinitionService;
import com.kthowns.mobidic.api.dictionary.service.VocabularyService;
import com.kthowns.mobidic.api.dictionary.service.WordService;
import com.kthowns.mobidic.api.preset.repository.PresetVocabularyRepository;
import com.kthowns.mobidic.api.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.api.quiz.service.CryptoService;
import com.kthowns.mobidic.api.security.jwt.JwtBlacklistService;
import com.kthowns.mobidic.api.security.jwt.JwtProperties;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.security.service.UserDetailsServiceImpl;
import com.kthowns.mobidic.api.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.api.statistic.service.StatisticService;
import com.kthowns.mobidic.api.statistic.util.DifficultyUtil;
import com.kthowns.mobidic.api.user.repository.UserRepository;
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
    public PresetVocabularyRepository presetVocabularyRepository() {
        return Mockito.mock(PresetVocabularyRepository.class);
    }

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