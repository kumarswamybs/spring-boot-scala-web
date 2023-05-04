package com.appNgeek.dto_entity_auto_rest_api.convertor.handler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import com.example.springboot.scala.web.config.ResponseBodyDTO;
import com.example.springboot.scala.web.config.UserCustomScalaSerializer;
import com.example.springboot.scala.web.dto.UserScalaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

public final class ResponseEntityToDTOHandlerMethodArgumentResolver extends RequestResponseBodyMethodProcessor {

    private final ApplicationContext applicationContext;

    private final ConversionService conversionService;

    private Map<Object,Object> serializationMap;

    @Autowired
    public ResponseEntityToDTOHandlerMethodArgumentResolver(final List<HttpMessageConverter<?>> messageConverters,
                                                            ApplicationContext applicationContext,Map<Object,Object> serializationMap) {
        super(messageConverters);
        this.applicationContext = applicationContext;
        this.conversionService = this.applicationContext.getBean(ConversionService.class);
        this.serializationMap = serializationMap;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (returnType.getMethodAnnotation(ResponseBodyDTO.class) != null || super.supportsReturnType(returnType));

    }

    @Override
    public void handleReturnValue(Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {

        Class<?> responseDTOType = getRequestBodyDTOType(returnType);
        if (responseDTOType != null) {
            Class<?> entityType = null;
            TypeDescriptor returnTypeDescriptor = new TypeDescriptor(returnType);
            ResolvableType resolvableType = returnTypeDescriptor.getResolvableType();
            entityType = resolvableType.getRawClass();
            UserCustomScalaSerializer customSerilization  = (UserCustomScalaSerializer) serializationMap.get(returnType.getParameterType());
            returnValue = customSerilization.serialize(null,null);
        }
        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    private Class<?> getRequestBodyDTOType(MethodParameter returnType) {
        for (Annotation ann : returnType.getMethodAnnotations()) {
            ResponseBodyDTO responseBodyDTO = AnnotationUtils.getAnnotation(ann, ResponseBodyDTO.class);
            if (responseBodyDTO != null) {
                return responseBodyDTO.value();
            }
        }
        return null;
    }

    public static TypeDescriptor page(Class<?> pageType, @Nullable TypeDescriptor elementTypeDescriptor) {
        Assert.notNull(pageType, "Page type must not be null");
        if (!Page.class.isAssignableFrom(pageType)) {
            throw new IllegalArgumentException("Page type must be a [org.springframework.data.domain.Page]");
        }
        ResolvableType element = (elementTypeDescriptor != null ? elementTypeDescriptor.getResolvableType() : null);
        return new TypeDescriptor(ResolvableType.forClassWithGenerics(pageType, element), null, null);
    }
}