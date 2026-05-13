package com.kthowns.mobidic.term.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TermType {
    SERVICE("서비스 이용 약관"),
    PRIVACY("개인정보 처리방침"),
    MARKETING("마케팅 정보 수신 동의"),
    LOCATION("위치정보 이용 약관");

    private final String name;
}
