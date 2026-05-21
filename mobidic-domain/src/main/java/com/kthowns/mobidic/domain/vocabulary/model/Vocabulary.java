package com.kthowns.mobidic.domain.vocabulary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vocabulary {
    private UUID id;
    private UUID userId;
    private String title;
    private String description;
    private Long wordCount;
    private LocalDateTime createdAt;

    public void addWordCount() {
        if (this.wordCount == null) this.wordCount = 0L;
        this.wordCount++;
    }

    public void removeWordCount() {
        if (this.wordCount == null || this.wordCount <= 0) {
            this.wordCount = 0L;
            return;
        }
        this.wordCount--;
    }
}
