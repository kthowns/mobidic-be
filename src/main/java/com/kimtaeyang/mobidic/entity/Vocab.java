package com.kimtaeyang.mobidic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="vocab")
public class Vocab {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}