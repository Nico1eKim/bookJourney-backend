package com.example.bookjourneybackend.domain.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class favoriteGenres {

    @NotBlank(message = "장르 선택은 필수입니다.")
    private String genreName;
}