package com.example.bookjourneybackend.domain.user.domain;

import lombok.Getter;

@Getter
public enum EmailContentTemplate {

    AUTH_CODE_EMAIL("[책산책] 책산책 인증 번호를 확인해주세요.",
            "회원님의 인증번호는 %s입니다.\n해당 인증번호를 %s까지 입력해주세요.");

    private final String title;
    private final String content;

    EmailContentTemplate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getContent(String authCode, String expirationTime) {
        return String.format(content, authCode, expirationTime);
    }

}
