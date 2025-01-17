package com.example.bookjourneybackend.domain.book.domain;

import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_image")
@Getter
@NoArgsConstructor
public class BookImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookImageId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Integer size;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Builder
    public BookImage(Long bookImageId, String imageUrl, String path, Integer size, Book book) {
        this.bookImageId = bookImageId;
        this.imageUrl = imageUrl;
        this.path = path;
        this.size = size;
        this.book = book;
    }
}
