package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBookBestSellersResponse {

    private List<BestSellerImageUrl> bestSellerList;

    public static GetBookBestSellersResponse of(List<BestSellerImageUrl> bestSellerList){
        return new GetBookBestSellersResponse(bestSellerList);
    }

}
