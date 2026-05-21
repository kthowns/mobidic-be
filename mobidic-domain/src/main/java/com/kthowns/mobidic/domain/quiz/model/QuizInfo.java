package com.kthowns.mobidic.domain.quiz.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class QuizInfo {
    private String token;
    private String stem;
    private List<String> options;
    private long expMil;
}
