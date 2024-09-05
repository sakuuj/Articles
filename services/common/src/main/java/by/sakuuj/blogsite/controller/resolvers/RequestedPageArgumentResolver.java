package by.sakuuj.blogsite.controller.resolvers;

import by.sakuuj.blogsite.exception.ControllerLayerException;
import by.sakuuj.blogsite.exception.ControllerLayerExceptionMessage;
import by.sakuuj.blogsite.paging.RequestedPage;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestedPageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(RequestedPage.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        String pageSizeParameter = webRequest.getParameter("page-size");
        String pageNumberParameter = webRequest.getParameter("page-number");

        if (pageSizeParameter == null) {
            throw new ControllerLayerException(ControllerLayerExceptionMessage.MISSING_PAGE_SIZE);
        }

        if (pageNumberParameter == null) {
            throw new ControllerLayerException(ControllerLayerExceptionMessage.MISSING_PAGE_NUMBER);
        }

        int pageSize = parseInt(
                pageSizeParameter,
                new ControllerLayerException(ControllerLayerExceptionMessage.PAGE_SIZE_SHOULD_BE_INTEGER)
        );

        int pageNumber = parseInt(
                pageNumberParameter,
                new ControllerLayerException(ControllerLayerExceptionMessage.PAGE_NUMBER_SHOULD_BE_INTEGER)
        );


        return RequestedPage.aPage()
                .withNumber(pageNumber)
                .withSize(pageSize);
    }

    private static int parseInt(String stringToParse, RuntimeException exceptionOnFail) {
        try {
            return Integer.parseInt(stringToParse);
        } catch (NumberFormatException ex) {
            throw exceptionOnFail;
        }
    }
}
