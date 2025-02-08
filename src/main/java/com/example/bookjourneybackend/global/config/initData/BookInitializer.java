package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookInitializer {

    private final BookRepository bookRepository;
    private final AladinApiUtil aladinApiUtil;


    private final List<String> dummyBestsellerIsbnList = List.of("9791130662480", "9788934900665","9788989024750","9788964076705",
            "9791141085568","9791137206717","9788998336493","9788910107002"
            ,"9788992426299","9791136787262","9788993690248","9788957576007",
            "9788996612209","9788934966784","9791169211536","9788945214263");

    private final List<String> dummyNomalIsbnList = List.of("9791166688621", "9791155817346","9791193937396","9791169090780",
            "9791158512132","9791191887259","9791194172093","9791172132057"
            ,"9788901289564","9791136791870","9791198528322","9791193712610",
            "9788962626391","9791193794906","9791169213332","9791198807281");

    public void initializeBooks() {

        for (int i = 0; i < dummyNomalIsbnList.size(); i++) {

            String requestUrl = aladinApiUtil.buildLookUpApiUrl(dummyBestsellerIsbnList.get(i));
            String currentResponse = aladinApiUtil.requestBookInfoFromAladinApi(requestUrl);

            Book book = aladinApiUtil.parseAladinApiResponseToBook(currentResponse,false,0);
            book.setBestSeller(true);
            bookRepository.save(book);

            requestUrl = aladinApiUtil.buildLookUpApiUrl(dummyNomalIsbnList.get(i));
            currentResponse = aladinApiUtil.requestBookInfoFromAladinApi(requestUrl);

            book = aladinApiUtil.parseAladinApiResponseToBook(currentResponse,false,0);
            bookRepository.save(book);

        }
    }

    // 고유한 ISBN 생성 (UUID 활용)
    private String generateUniqueIsbn() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 13); // 13자리 ISBN 생성
    }
}
