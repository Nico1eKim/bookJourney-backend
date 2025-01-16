package com.example.bookjourneybackend.global.entity;

import lombok.Getter;

@Getter
public enum UserRole {

    HOST("호스트"), MEMBER("팀원");

    private String type;

    UserRole(String type) {
        this.type = type;
    }

    public static UserRole from(String type) {
        for (UserRole postType : UserRole.values()) {
            if (postType.getType().equals(type)) {
                return postType;
            }
        }
        //TODO 예외 엔티티 작성
        //throw new CustomException(ErrorCode.NO_SUCH_TYPE);
        return null;
    }


}
