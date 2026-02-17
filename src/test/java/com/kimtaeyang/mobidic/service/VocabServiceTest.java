package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.AddVocabRequestDto;
import com.kimtaeyang.mobidic.dto.VocabDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.repository.VocabRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {VocabService.class, VocabServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class VocabServiceTest {
    @Autowired
    private VocabRepository vocabRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private VocabService vocabService;

    @Test
    @DisplayName("[VocabService] Add vocab success")
    void addVocabSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        AddVocabRequestDto request = AddVocabRequestDto.builder()
                .title("title")
                .description("description")
                .build();
        ArgumentCaptor<Vocab> captor =
                ArgumentCaptor.forClass(Vocab.class);

        //given
        given(memberRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Member.class)));
        given(vocabRepository.countByTitleAndMember(anyString(), any(Member.class)))
                .willReturn(0);
        given(vocabRepository.save(any(Vocab.class)))
                .willAnswer(invocation -> {
                    Vocab vocabArg = invocation.getArgument(0);
                    vocabArg.setId(vocabId);
                    return vocabArg;
                });

        //when
        VocabDto response = vocabService.addVocab(UUID.randomUUID(), request);

        //then
        verify(vocabRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(vocabId, response.getId());
    }

    @Test
    @DisplayName("[VocabService] Get vocabs by member id success")
    void getVocabsByMemberIdSuccess() {
        resetMock();

        Vocab defaultVocab = Vocab.builder()
                .member(Mockito.mock(Member.class))
                .title("title")
                .description("description")
                .build();

        ArrayList<Vocab> vocabs = new ArrayList<>();
        vocabs.add(defaultVocab);

        //given
        given(memberRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Member.class)));
        given(vocabRepository.findByMember(any(Member.class)))
                .willReturn(vocabs);

        //when
        List<VocabDto> response = vocabService.getVocabsByMemberId(UUID.randomUUID());

        //then
        assertEquals(vocabs.getFirst().getMember().getId(), response.getFirst().getMemberId());
        assertEquals(vocabs.getFirst().getTitle(), response.getFirst().getTitle());
        assertEquals(vocabs.getFirst().getDescription(), response.getFirst().getDescription());
    }

    @Test
    @DisplayName("[VocabService] Get vocab by vocab id success")
    void getVocabByVocabIdSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        Vocab defaultVocab = Vocab.builder()
                .id(vocabId)
                .member(Mockito.mock(Member.class))
                .title("title")
                .description("description")
                .build();

        //given
        given(vocabRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultVocab));

        //when
        VocabDto response = vocabService.getVocabById(vocabId);

        //then
        assertEquals(vocabId, response.getId());
        assertEquals(defaultVocab.getTitle(), response.getTitle());
        assertEquals(defaultVocab.getDescription(), response.getDescription());
    }

    @Test
    @DisplayName("[VocabService] Update vocab success")
    void updateVocabSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        Vocab defaultVocab = Vocab.builder()
                .id(vocabId)
                .member(Mockito.mock(Member.class))
                .title("title")
                .description("description")
                .build();

        AddVocabRequestDto request =
                AddVocabRequestDto.builder()
                        .title("title2")
                        .description("description2")
                        .build();

        ArgumentCaptor<Vocab> captor =
                ArgumentCaptor.forClass(Vocab.class);

        //given
        given(vocabRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultVocab));
        given(vocabRepository.countByTitleAndMemberAndIdNot(anyString(), any(Member.class), any(UUID.class)))
                .willReturn(0);
        given(vocabRepository.save(any(Vocab.class)))
                .willAnswer(invocation -> {
                    Vocab vocabArg = invocation.getArgument(0);
                    vocabArg.setTitle(request.getTitle());
                    vocabArg.setDescription(request.getDescription());
                    return vocabArg;
                });

        //when
        VocabDto response =
                vocabService.updateVocab(vocabId, request);

        //then
        verify(vocabRepository, times(1))
                .save(captor.capture());
        assertEquals(vocabId, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VocabRepository vocabRepository() {
            return Mockito.mock(VocabRepository.class);
        }

        @Bean
        public MemberRepository memberRepository() {
            return Mockito.mock(MemberRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(vocabRepository, memberRepository);
    }
}