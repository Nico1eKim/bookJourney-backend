package com.example.bookjourneybackend.domain.user.controller;

import com.example.bookjourneybackend.domain.user.service.S3Service;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/images")
public class UserImageController {

    private final S3Service s3Service;

    @PostMapping
    public BaseResponse<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = s3Service.getImageFromUser(image);
        return BaseResponse.ok(profileImage);
    }

    @DeleteMapping
    public BaseResponse<?> s3delete(@RequestParam String addr){
        s3Service.deleteImageFromS3(addr);
        return BaseResponse.ok(null);
    }
}
