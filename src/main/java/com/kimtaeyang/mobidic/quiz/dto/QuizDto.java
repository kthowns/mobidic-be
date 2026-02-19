package com.kimtaeyang.mobidic.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizDto {
    private String token;
    private String stem;
    private long expMil;
    private List<String> options;
}
