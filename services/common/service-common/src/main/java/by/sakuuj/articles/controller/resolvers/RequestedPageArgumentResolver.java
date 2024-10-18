package by.sakuuj.articles.controller.resolvers;

import by.sakuuj.articles.paging.RequestedPage;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import java.util.Optional;

public class RequestedPageArgumentResolver extends RequestParamMethodArgumentResolver {

    public RequestedPageArgumentResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) {

        String pageSize = Optional.ofNullable(request.getParameter("page-size"))
                .orElseThrow(() -> new IllegalStateException("'page-size' request parameter is not specified"));
        int parsedPageSize = parseInt(pageSize);

        String pageNumber = Optional.ofNullable(request.getParameter("page-number"))
                .orElseThrow(() -> new IllegalStateException("'page-number' request parameter is not specified"));
        int parsedPageNumber = parseInt(pageNumber);

        return RequestedPage.builder()
                .size(parsedPageSize)
                .number(parsedPageNumber)
                .build();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(RequestedPage.class);
    }

    private static int parseInt(String stringToParse) {
        try {
            return Integer.parseInt(stringToParse);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException();
        }
    }
}
