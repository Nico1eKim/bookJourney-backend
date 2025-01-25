package com.example.bookjourneybackend.domain.room.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostRoomCreateRequest {

    private boolean isPublic;

    @NotBlank(message = "방 제목을 입력해주세요.")
    @Size(max = 20, message = "방 제목은 20자 이내입니다.")
    private String roomName;

    @NotNull(message = "시작일자를 작성해주세요.")
    private String progressStartDate;

    @NotNull(message = "종료일자를 작성해주세요.")
    private String progressEndDate;

    @Min(value = 1, message = "모집 최소 인원은 1명입니다.")
    @Max(value = 50, message = "모집 최대 인원은 50명입니다.")
    private int recruitCount;

    @Pattern(regexp = "\\d{4}", message = "비밀번호는 4개의 숫자로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "ISBN cannot be blank.")
    @Pattern(regexp = "\\d{10,13}", message = "ISBN은 10이나 13자리로 이루어집니다.")
    private String isbn;
}
