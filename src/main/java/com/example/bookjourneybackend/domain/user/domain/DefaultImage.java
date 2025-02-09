package com.example.bookjourneybackend.domain.user.domain;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.Random;

@Getter
public enum DefaultImage {
    //todo 추후에 디자이너한테 기본이미지 4개 받으면 S3에 업로드 후 url 수정
    DEFAULT1("https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png"),
    DEFAULT2("https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png"),
    DEFAULT3("https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png"),
    DEFAULT4("https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png");

    private final String imageUrl;

    DefaultImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 사용자 기본 이미지 랜덤 배졍
    public static String assignRandomUserImage() {
        DefaultImage[] defaultImages = DefaultImage.values();
        return defaultImages[new Random(System.currentTimeMillis()).nextInt(defaultImages.length)].getImageUrl();
    }
}
