package com.example.bookjourneybackend.domain.user;

import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_image")
@Getter
@NoArgsConstructor
public class UserImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_image_id")
    private Long userImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Builder
    public UserImage(Long userImageId, User user, String imageUrl, String path, Integer size) {
        this.userImageId = userImageId;
        this.user = user;
        this.imageUrl = imageUrl;
        this.path = path;
        this.size = size;
    }
}
