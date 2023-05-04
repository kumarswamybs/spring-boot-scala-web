package com.example.springboot.scala.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.util.*;

public class DTOModelMapper extends RequestResponseBodyMethodProcessor {

     MappingJackson2HttpMessageConverter defaultOne;

     private Map<Object,Object> deserializeMap = new HashMap<>();

    private Map<Object,Object> serializeMap = new HashMap<>();
    
    private  ApplicationContext applicationContext;

    private final ConversionService conversionService;

    public DTOModelMapper(MappingJackson2HttpMessageConverter converter, Map<Object,Object> map,
                          Map<Object,Object> serializeMap,ApplicationContext applicationContext) {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        this.defaultOne = converter;
        this.deserializeMap = map;
        this.serializeMap = serializeMap;
        this.applicationContext = applicationContext;
        this.conversionService = this.applicationContext.getBean(ConversionService.class);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Deserializer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonParser parser = getJsonParser(webRequest);
       // Object targetClass = getTargetClass(parameter);
        StdDeserializer customDeserializer = (StdDeserializer) deserializeMap.get(parameter.getParameterType());
        return  customDeserializer.deserialize(parser,null);
    }

    @Override
    public void handleReturnValue(Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {

        Class<?> responseDTOType = getRequestBodyDTOType(returnType);
        if (responseDTOType != null) {

            Class<?> entityType = null;

            TypeDescriptor returnTypeDescriptor = new TypeDescriptor(returnType);
            ResolvableType resolvableType = returnTypeDescriptor.getResolvableType();
            StdDeserializer customSerializer = (StdDeserializer) serializeMap.get(responseDTOType.getClass());
            if (resolvableType.hasGenerics()) {
                entityType = resolvableType.getGenerics()[0].getRawClass();
                if (TypeDescriptor.valueOf(resolvableType.getRawClass()).isAssignableTo(TypeDescriptor.valueOf(Page.class))) {
                    TypeDescriptor sourceType = page(Page.class, TypeDescriptor.valueOf(entityType));
                    TypeDescriptor targetType = page(Page.class, TypeDescriptor.valueOf(responseDTOType));
                    returnValue = conversionService.convert(returnValue, sourceType, targetType);
                } else if (returnTypeDescriptor.isCollection()) {
                    TypeDescriptor sourceType = TypeDescriptor.collection(returnTypeDescriptor.getResolvableType().getRawClass(),
                            TypeDescriptor.valueOf(entityType));
                    TypeDescriptor targetType = TypeDescriptor.collection(returnTypeDescriptor.getResolvableType().getRawClass(),
                            TypeDescriptor.valueOf(responseDTOType));
                    returnValue = conversionService.convert(returnValue, sourceType, targetType);
                }
            } else {
                entityType = resolvableType.getRawClass();
                if (conversionService.canConvert(entityType, responseDTOType)) {
                    returnValue = conversionService.convert(returnValue, responseDTOType);
                }
            }
        }

        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    public static TypeDescriptor page(Class<?> pageType, @Nullable TypeDescriptor elementTypeDescriptor) {
        Assert.notNull(pageType, "Page type must not be null");
        if (!Page.class.isAssignableFrom(pageType)) {
            throw new IllegalArgumentException("Page type must be a [org.springframework.data.domain.Page]");
        }
        ResolvableType element = (elementTypeDescriptor != null ? elementTypeDescriptor.getResolvableType() : null);
        return new TypeDescriptor(ResolvableType.forClassWithGenerics(pageType, element), null, null);
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
    public JsonParser getJsonParser(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
        EmptyBodyCheckingHttpInputMessage message = new EmptyBodyCheckingHttpInputMessage(inputMessage);
        InputStream inputStream = StreamUtils.nonClosing(message.getBody());
        return defaultOne.getObjectMapper().getFactory().createParser(inputStream);
    }

    protected Object getTargetClass(MethodParameter parameter) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Deserializer dtoType = AnnotationUtils.getAnnotation(ann, Deserializer.class);
            if (dtoType != null) {
                return dtoType.value();
            }
        }
        throw new RuntimeException();
    }

    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {
        private final HttpHeaders headers;
        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = inputStream.read() != -1 ? inputStream : null;
                inputStream.reset();
            } else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                } else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }

        }

        public HttpHeaders getHeaders() {
            return this.headers;
        }

        public InputStream getBody() {
            return this.body != null ? this.body : StreamUtils.emptyInput();
        }

        public boolean hasBody() {
            return this.body != null;
        }
    }
}
