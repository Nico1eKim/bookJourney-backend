package com.example.bookjourneybackend.global.resolver;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.global.exception.GlobalException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Component
public class GetBookListRequestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(GetBookListRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String searchTerm = webRequest.getParameter("searchTerm");
        String genre = webRequest.getParameter("genre");
        String queryType = webRequest.getParameter("queryType");

        String pageParam = webRequest.getParameter("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 0 : Integer.parseInt(pageParam);

        page++;

        validateRequest(searchTerm, page, queryType);

        return GetBookListRequest.builder()
                .searchTerm(searchTerm)
                .genre(genre)
                .queryType(queryType)
                .page(page)
                .build();

    }

    //dto 유효성 검증
    private void validateRequest(String searchTerm, int page, String queryType) {
        // 검색어가 비어있는지 검사
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new GlobalException(EMPTY_SEARCH_TERM);
        }
        // 페이지가 1보다 작은지 검사
        if (!(page >= 1)) {
            throw new GlobalException(INVALID_PAGE);
        }
        // queryType 유효성 검사
        if (queryType == null || queryType.isBlank() ||
                !(queryType.equals("Title") || queryType.equals("Author"))) {
            throw new GlobalException(INVALID_QUERY_TYPE);
        }
    }

}
