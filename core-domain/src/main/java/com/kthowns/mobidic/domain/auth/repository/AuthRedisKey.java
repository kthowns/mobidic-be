package com.kthowns.mobidic.domain.auth.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthRedisKey {
    DEACTIVATED("deactivated_user");

    private final String prefix;

    @Override
    public String toString() {
        return prefix;
    }
}
