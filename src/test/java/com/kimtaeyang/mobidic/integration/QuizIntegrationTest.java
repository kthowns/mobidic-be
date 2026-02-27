package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.*;
import com.kimtaeyang.mobidic.dictionary.model.WordWithDefinitions;
import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
import com.kimtaeyang.mobidic.quiz.dto.QuizDto;
import com.kimtaeyang.mobidic.quiz.dto.QuizRateRequest;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuizIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("[Quiz][Integration] Ox quiz generate test")
    void oxQuizGenerateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID vocabId = addVocabAndGetId(token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefinitions> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quizs/ox")
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuizDto> resultQuestions = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        assertEquals(savedWords.size(), resultQuestions.size());
    }

    @Test
    @DisplayName("[Quiz][Integration] Ox quiz rate test")
    void oxQuizRateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID vocabId = addVocabAndGetId(token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};

        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefinitions> savedWords = addWordsAndGetDetails(
                sampleWords,
                sampleDefs,
                sampleParts,
                vocabId,
                token
        );

        MvcResult quizResult = mockMvc.perform(get("/api/quizs/ox")
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuizDto> quizsResponse = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        for (int i = 0; i < savedWords.size(); i++) {
            String correctAnswer = findCorrectAnswer(quizsResponse.get(i), sampleWords, sampleDefs);
            boolean answer = correctAnswer.equals(quizsResponse.get(i).getOptions().getFirst());

            QuizRateRequest quizRateRequest = QuizRateRequest.builder()
                    .token(quizsResponse.get(i).getToken())
                    .answer(answer ? "1" : "0")
                    .build();

            mockMvc.perform(post("/api/quizs/rate")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(quizRateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isCorrect")
                            .value(true))
                    .andExpect(jsonPath("$.data.correctAnswer")
                            .value(answer ? "1" : "0"));
        }
    }

    private String findCorrectAnswer(QuizDto quiz, String[] orgWords, String[] orgDefs) {
        String stem = quiz.getStem();
        String correctAnswer = "";

        for (int i = 0; i < orgWords.length; i++) {
            if (stem.equals(orgWords[i])) {
                correctAnswer = orgDefs[i];
            }
        }

        return correctAnswer;
    }

    @Test
    @DisplayName("[Quiz][Integration] Blank quiz generate test")
    void blankQuizGenerateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID vocabId = addVocabAndGetId(token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefinitions> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quizs/blank")
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuizDto> resultQuestions = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        assertEquals(savedWords.size(), resultQuestions.size());
        for (QuizDto quizDto : resultQuestions) {
            String stem = quizDto.getStem();
            int cnt = 0;
            for (int i = 0; i < stem.length(); i++) {
                if (stem.charAt(i) == '_') {
                    cnt++;
                }
            }
            assertEquals(stem.length() / 2 + 1, cnt);
        }
    }

    @Test
    @DisplayName("[Quiz][Integration] Blank quiz rate test")
    void blankQuizRateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID vocabId = addVocabAndGetId(token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefinitions> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quizs/blank")
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuizDto> quizs = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        for (int i = 0; i < savedWords.size(); i++) {
            String stem = quizs.get(i).getStem();
            String fullAnswer = "";
            for (String sample : sampleWords) {
                boolean isSame = true;
                if (sample.length() != stem.length()) {
                    continue;
                }
                for (int j = 0; j < sample.length(); j++) {
                    if (!(sample.charAt(j) == stem.charAt(j) || stem.charAt(j) == '_')) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    fullAnswer = sample;
                    break;
                }
            }
            StringBuilder realAnswer = new StringBuilder();
            for (int j = 0; j < stem.length(); j++) {
                if (stem.charAt(j) == '_') {
                    realAnswer.append(fullAnswer.charAt(j));
                }
            }

            QuizRateRequest rateQuizRateRequest = QuizRateRequest.builder()
                    .token(quizs.get(i).getToken())
                    .answer(realAnswer.toString())
                    .build();

            MvcResult rateResult = mockMvc.perform(post("/api/quizs/rate")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rateQuizRateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isCorrect")
                            .value(true))
                    .andReturn();
        }
    }

    private List<WordWithDefinitions> addWordsAndGetDetails(String[] sampleWords, String[] sampleDefs,
                                                            PartOfSpeech[] sampleParts, UUID vocabId, String token) throws Exception {
        for (int i = 0; i < sampleWords.length; i++) {
            UUID wordId = addWordAndGetId(vocabId, token, sampleWords[i]);
            UUID defId = addDefAndGetId(wordId, token, sampleDefs[i], sampleParts[i]);
        }

        MvcResult wordsResult = mockMvc.perform(get("/api/words/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabId.toString())
                ).andExpect(status().isOk())
                .andReturn();
        String json = wordsResult.getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(json).path("data");
        List<WordDto> wordDtos = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        List<WordWithDefinitions> wordWithDefs = new ArrayList<>();
        for (WordDto wordDto : wordDtos) {
            wordsResult = mockMvc.perform(get("/api/definitions/all")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .param("wordId", wordDto.getId().toString())
                    ).andExpect(status().isOk())
                    .andReturn();
            json = wordsResult.getResponse().getContentAsString();
            data = objectMapper.readTree(json).path("data");
            List<DefinitionDto> definitionDtos = objectMapper.readValue(data.toString(), new TypeReference<>() {
            });
            wordWithDefs.add(new WordWithDefinitions(wordDto, definitionDtos));
        }

        return wordWithDefs;
    }

    private UUID addVocabAndGetId(String token) throws Exception {
        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String vocabId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(vocabId);
    }

    private UUID addWordAndGetId(UUID vocabId, String token, String exp) throws Exception {
        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression(exp)
                .build();

        MvcResult wordResult = mockMvc.perform(post("/api/words/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String wordId = objectMapper.readTree(wordResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(wordId);
    }

    private UUID addDefAndGetId(UUID wordId, String token, String def, PartOfSpeech part) throws Exception {
        AddDefinitionRequestDto addDefRequest = AddDefinitionRequestDto.builder()
                .definition(def)
                .part(part)
                .build();

        MvcResult defResult = mockMvc.perform(post("/api/definitions/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String defId = objectMapper.readTree(defResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(defId);
    }

    private String loginAndGetToken(String email, String nickname) throws Exception {
        SignUpRequestDto joinRequest = SignUpRequestDto.builder()
                .email(email)
                .nickname(nickname)
                .password("testTest1")
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("accessToken").asText();
    }
}
