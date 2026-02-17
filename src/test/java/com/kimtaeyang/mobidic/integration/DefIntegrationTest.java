package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.dto.AddDefRequestDto;
import com.kimtaeyang.mobidic.dto.AddVocabRequestDto;
import com.kimtaeyang.mobidic.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
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

import java.util.UUID;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.UNAUTHORIZED;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_DEFINITION;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.INVALID_REQUEST_BODY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class DefIntegrationTest {
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
    @DisplayName("[Def][Integration] Add def test")
    void addDefTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID wordId = addWordAndGetId(memberId, token);

        AddDefRequestDto addDefRequest = AddDefRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        //Success
        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.definition")
                        .value(addDefRequest.getDefinition()))
                .andExpect(jsonPath("$.data.part")
                        .value(addDefRequest.getPart().toString()));

        //Fail with duplicated definition
        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_DEFINITION.getMessage()));

        //Fail without token
        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(post("/api/def/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid pattern
        addDefRequest.setDefinition(UUID.randomUUID().toString());
        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.definition")
                        .value("Invalid definition pattern"));
    }

    @Test
    @DisplayName("[Def][Integration] Get defs by word id test")
    void getDefsByWordIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID wordId = addWordAndGetId(memberId, token);

        AddDefRequestDto addDefRequest = AddDefRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk());

        //Success
        mockMvc.perform(get("/api/def/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data[0].definition")
                        .value(addDefRequest.getDefinition()))
                .andExpect(jsonPath("$.data[0].part")
                        .value(addDefRequest.getPart().toString()))
                .andExpect(jsonPath("$.data[0].wordId")
                        .value(wordId.toString()));

        //Fail without token
        mockMvc.perform(get("/api/def/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("wId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/def/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("wId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/def/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }


    @Test
    @DisplayName("[Def][Integration] Update def test")
    void updateDefTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID wordId = addWordAndGetId(memberId, token);

        AddDefRequestDto addDefRequest = AddDefRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();
        AddDefRequestDto addDefRequest2 = AddDefRequestDto.builder()
                .definition("definition2")
                .part(PartOfSpeech.NOUN)
                .build();

        MvcResult result = mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult result2 = mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest2)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String defId = objectMapper.readTree(json).path("data").path("id").asText();
        json = result2.getResponse().getContentAsString();
        String defId2 = objectMapper.readTree(json).path("data").path("id").asText();

        //Success
        addDefRequest.setPart(PartOfSpeech.VERB);
        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.definition")
                        .value(addDefRequest.getDefinition()))
                .andExpect(jsonPath("$.data.part")
                        .value(addDefRequest.getPart().toString()));
        addDefRequest.setPart(PartOfSpeech.VERB);

        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.definition")
                        .value(addDefRequest.getDefinition()))
                .andExpect(jsonPath("$.data.part")
                        .value(addDefRequest.getPart().toString()));

        //Fail with duplicated definition
        mockMvc.perform(patch("/api/def/" + defId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_DEFINITION.getMessage()));

        //Fail without token
        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/def/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid definition pattern
        addDefRequest.setDefinition(UUID.randomUUID().toString());
        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.definition")
                        .value("Invalid definition pattern"));

        //Fail with invalid part pattern
        String wrongRequest = "{\"definition\":\"some def\","
                + "\"part\":\"STRING\"}";

        mockMvc.perform(patch("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(wrongRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()));
    }

    @Test
    @DisplayName("[Def][Integration] Delete def test")
    void deleteDefTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID wordId = addWordAndGetId(memberId, token);

        AddDefRequestDto addDefRequest = AddDefRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        MvcResult result = mockMvc.perform(post("/api/def/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addDefRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String defId = objectMapper.readTree(json).path("data").path("id").asText();


        //Fail without token
        mockMvc.perform(delete("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(delete("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Success
        mockMvc.perform(delete("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(defId))
                .andExpect(jsonPath("$.data.definition")
                        .value(addDefRequest.getDefinition()))
                .andExpect(jsonPath("$.data.part")
                        .value(addDefRequest.getPart().toString()))
                .andExpect(jsonPath("$.data.wordId")
                        .value(wordId.toString()));

        //Fail with no resource
        mockMvc.perform(delete("/api/def/" + defId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    private UUID addWordAndGetId(UUID memberId, String token) throws Exception {
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

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("expression")
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
// Resource api integration test convention
//Success
// -> OK
//Fail without token
// -> UNAUTHORIZED
//Fail with unauthorized token
// -> UNAUTHORIZED
//Fail with no resource
// -> UNAUTHORIZED
//Fail with invalid pattern
// -> INVALID_REQUEST_BODY