package com.kthowns.mobidic.storage.term.jpaentity;

import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "terms")
@EntityListeners(AuditingEntityListener.class)
public class TermJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TermType type;

    @Column(name = "version", nullable = false, updatable = false)
    private String version;

    @Column(name = "is_required", nullable = false, updatable = false)
    @Builder.Default
    private boolean required = false;

    @Setter
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public Term toModel() {
        return Term.builder()
                .id(this.getId())
                .type(this.getType())
                .version(this.getVersion())
                .required(this.required)
                .content(this.getContent())
                .createdAt(this.getCreatedAt())
                .build();
    }

    public SimpleTerm toSimpleModel() {
        return SimpleTerm.builder()
                .id(this.id)
                .type(this.type)
                .version(this.version)
                .required(this.required)
                .contentUri("/terms/" + this.type.name().toLowerCase() + "?version=" + this.version)
                .createdAt(this.createdAt)
                .build();
    }
}
