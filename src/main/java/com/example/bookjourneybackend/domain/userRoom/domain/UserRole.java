package com.example.bookjourneybackend.domain.userRoom.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.NO_SUCH_TYPE_USER;

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
        throw new GlobalException(NO_SUCH_TYPE_USER);
    }


}
