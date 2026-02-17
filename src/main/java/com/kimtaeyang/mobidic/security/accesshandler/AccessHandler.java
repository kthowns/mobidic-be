package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public abstract class AccessHandler {
    public final boolean ownershipCheck(UUID resourceId) {
        return isResourceOwner(resourceId);
    }

    public final UUID getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((Member) auth.getPrincipal()).getId();
    }
    abstract boolean isResourceOwner(UUID resourceId);
}
