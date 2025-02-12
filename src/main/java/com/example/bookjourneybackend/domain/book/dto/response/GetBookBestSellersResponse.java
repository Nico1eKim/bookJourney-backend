package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBookBestSellersResponse {

    private List<BestSellerInfo> bestSellerList;
    private String nickName;

    public static GetBookBestSellersResponse of(List<BestSellerInfo> bestSellerList, String nickName){
        return new GetBookBestSellersResponse(bestSellerList,nickName);
    }

}
