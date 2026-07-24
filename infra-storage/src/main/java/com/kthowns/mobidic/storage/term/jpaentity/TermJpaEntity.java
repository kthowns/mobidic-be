package com.kthowns.mobidic.storage.term.jpaentity;

import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "terms")
public class TermJpaEntity extends BaseAuditingEntity {
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
    private boolean required;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    public static TermJpaEntity createFromModel(Term term) {
        return TermJpaEntity.builder()
                .id(term.id())
                .type(term.type())
                .version(term.version())
                .required(term.required())
                .content(term.content())
                .active(true)
                .build();
    }

    public Term toModel() {
        return new Term(
                this.id,
                this.type,
                this.version,
                this.required,
                this.content,
                AuditTime.of(getCreatedAt(), getUpdatedAt())
        );
    }

    public SimpleTerm toSimpleModel() {
        return new SimpleTerm(
                this.id,
                this.type,
                this.version,
                this.required,
                "/terms/" + this.type.name().toLowerCase() + "?version=" + this.version,
                AuditTime.of(getCreatedAt(), getUpdatedAt())
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TermJpaEntity(
            Long id,
            TermType type,
            String version,
            boolean required,
            boolean active,
            String content
    ) {

        this.id = id;
        this.type = type;
        this.version = version;
        this.required = required;
        this.active = active;
        this.content = content;
    }
}
