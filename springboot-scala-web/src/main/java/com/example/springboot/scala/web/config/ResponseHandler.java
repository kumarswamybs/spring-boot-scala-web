package com.example.springboot.scala.web.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

public final class ResponseHandler extends RequestResponseBodyMethodProcessor {

    @Autowired
    public ResponseHandler(final List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (returnType.getMethodAnnotation(Serializer.class) != null || super.supportsReturnType(returnType));

    }

    @Override
    public void handleReturnValue(Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {

        Serializer responseBodyDTO = returnType.getMethodAnnotation(Serializer.class);
        if (responseBodyDTO == null) {
            responseBodyDTO = returnType.getContainingClass().getAnnotation(Serializer.class);
        }

        // Get the class specified in the @Serializer annotation
        Class<?> dtoClass = responseBodyDTO.value();

        // Create an instance of the specified class and set the response body
        try {
            UserCustomScalaSerializer customSerilization = (UserCustomScalaSerializer)dtoClass.newInstance();
            returnValue = customSerilization.serialize(returnValue);
            super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}