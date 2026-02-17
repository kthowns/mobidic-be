package com.kimtaeyang.mobidic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
    private UUID id;
    private UUID memberId;
    private UUID wordId;
    private String stem;
    private List<String> options;
    private String answer;
}
