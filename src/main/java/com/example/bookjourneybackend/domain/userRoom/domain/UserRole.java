package com.example.bookjourneybackend.domain.userRoom.domain;

import lombok.Getter;

@Getter
public enum UserRole {

    HOST("호스트"), MEMBER("팀원");

    private String type;

    UserRole(String type) {
        this.type = type;
    }

    public static UserRole from(String type) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getType().equals(type)) {
                return userRole;
            }
        }
        //TODO 예외 엔티티 작성
        //throw new CustomException(ErrorCode.NO_SUCH_TYPE);
        return null;
    }


}
