package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BestSellerInfo {
    private String imageUrl;
    private String isbn;
    private String genreName;
}
