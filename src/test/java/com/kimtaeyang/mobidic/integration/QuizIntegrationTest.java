package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.dto.*;
import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
import com.kimtaeyang.mobidic.model.WordWithDefs;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtUtil;
import com.kimtaeyang.mobidic.type.PartOfSpeech;
import org.junit.jupiter.api.AfterEach;
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
@ActiveProfiles("dev")
public class QuizIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("[Quiz][Integration] Ox quiz generate test")
    void oxQuizGenerateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefs> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quiz/generate//ox")
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuestionDto> resultQuestions = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        assertEquals(savedWords.size(), resultQuestions.size());
    }

    @Test
    @DisplayName("[Quiz][Integration] Ox quiz rate test")
    void oxQuizRateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefs> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quiz/generate/ox")
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuestionDto> quizs = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        for (int i = 0; i < savedWords.size(); i++) {
            QuestionRateDto.Request rateRequest = QuestionRateDto.Request.builder()
                    .token(quizs.get(i).getToken())
                    .answer(savedWords.get(i).getDefDtos().getFirst().getDefinition())
                    .build();

            mockMvc.perform(post("/api/quiz/rate/ox")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isCorrect")
                            .value(true))
                    .andExpect(jsonPath("$.data.correctAnswer")
                            .value(savedWords.get(i).getDefDtos().getFirst().getDefinition()));
        }
    }

    @Test
    @DisplayName("[Quiz][Integration] Blank quiz generate test")
    void blankQuizGenerateTest() throws Exception {
        String token = loginAndGetToken("email@test.com", "password1");
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefs> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quiz/generate/blank")
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuestionDto> resultQuestions = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        assertEquals(savedWords.size(), resultQuestions.size());
        for (QuestionDto questionDto : resultQuestions) {
            String stem = questionDto.getStem();
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
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);

        String[] sampleWords = {"Hello", "Apple", "Run", "Edit", "Amazing"};
        String[] sampleDefs = {"안녕", "사과", "뛰다", "편집하다", "개쩌는"};
        PartOfSpeech[] sampleParts = {PartOfSpeech.INTERJECTION, PartOfSpeech.NOUN, PartOfSpeech.VERB,
                PartOfSpeech.VERB, PartOfSpeech.ADJECTIVE};

        List<WordWithDefs> savedWords = addWordsAndGetDetails(sampleWords, sampleDefs, sampleParts
                , vocabId, token);

        MvcResult quizResult = mockMvc.perform(get("/api/quiz/generate/blank")
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(quizResult.getResponse().getContentAsString()).path("data");
        List<QuestionDto> quizs = objectMapper.readValue(data.toString(), new TypeReference<>() {
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

            QuestionRateDto.Request rateRequest = QuestionRateDto.Request.builder()
                    .token(quizs.get(i).getToken())
                    .answer(realAnswer.toString())
                    .build();

            MvcResult rateResult = mockMvc.perform(post("/api/quiz/rate/blank")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isCorrect")
                            .value(true))
                    .andReturn();
        }
    }

    private List<WordWithDefs> addWordsAndGetDetails(String[] sampleWords, String[] sampleDefs,
                                                     PartOfSpeech[] sampleParts, UUID vocabId, String token) throws Exception {
        for (int i = 0; i < sampleWords.length; i++) {
            UUID wordId = addWordAndGetId(vocabId, token, sampleWords[i]);
            UUID defId = addDefAndGetId(wordId, token, sampleDefs[i], sampleParts[i]);
        }

        MvcResult wordsResult = mockMvc.perform(get("/api/word/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString())
                ).andExpect(status().isOk())
                .andReturn();
        String json = wordsResult.getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(json).path("data");
        List<WordDto> wordDtos = objectMapper.readValue(data.toString(), new TypeReference<>() {
        });

        List<WordWithDefs> wordWithDefs = new ArrayList<>();
        for (WordDto wordDto : wordDtos) {
            wordsResult = mockMvc.perform(get("/api/def/all")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .param("wId", wordDto.getId().toString())
                    ).andExpect(status().isOk())
                    .andReturn();
            json = wordsResult.getResponse().getContentAsString();
            data = objectMapper.readTree(json).path("data");
            List<DefDto> defDtos = objectMapper.readValue(data.toString(), new TypeReference<>() {
            });
            wordWithDefs.add(new WordWithDefs(wordDto, defDtos));
        }

        return wordWithDefs;
    }

    private UUID addVocabAndGetId(UUID memberId, String token) throws Exception {
        AddVocabRequestDto addVocabRequest = AddVocabRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/vocab/" + memberId)
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

        MvcResult wordResult = mockMvc.perform(post("/api/word/" + vocabId)
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
        AddDefRequestDto addDefRequest = AddDefRequestDto.builder()
                .definition(def)
                .part(part)
                .build();

        MvcResult defResult = mockMvc.perform(post("/api/def/" + wordId)
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
        JoinRequestDto joinRequest = JoinRequestDto.builder()
                .email(email)
                .nickname(nickname)
                .password("testTest1")
                .build();

        LoginDto.Request loginRequest = LoginDto.Request.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        mockMvc.perform(post("/api/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("token").asText();
    }
}
