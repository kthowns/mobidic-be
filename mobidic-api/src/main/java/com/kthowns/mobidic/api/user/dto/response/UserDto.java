package com.kthowns.mobidic.api.user.dto.response;

import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static UserDto fromEntity(UserJpaEntity userJpaEntity) {
        return UserDto.builder()
                .id(userJpaEntity.getId())
                .email(userJpaEntity.getEmail())
                .nickname(userJpaEntity.getNickname())
                .createdAt(userJpaEntity.getCreatedAt())
                .build();
    }
}
