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

    @Size(max = 20, message = "방 제목은 20자 이내입니다.")
    private String roomName;

    private String progressStartDate;

    private String progressEndDate;

    @Min(value = 1, message = "모집 최소 인원은 1명입니다.")
    @Max(value = 50, message = "모집 최대 인원은 50명입니다.")
    private int recruitCount;

    private Integer password;

    @NotBlank(message = "ISBN cannot be blank.")
    @Pattern(regexp = "\\d{10,13}", message = "ISBN은 10이나 13자리로 이루어집니다.")
    private String isbn;
}
