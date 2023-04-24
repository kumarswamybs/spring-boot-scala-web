package com.example.springboot.scala.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DTOModelMapper extends RequestResponseBodyMethodProcessor {

     MappingJackson2HttpMessageConverter defaultOne;

     private Map<Object,Object> deserializeMap = new HashMap<>();


    public DTOModelMapper(MappingJackson2HttpMessageConverter converter, Map<Object,Object> map) {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        this.defaultOne = converter;
        this.deserializeMap = map;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonParser parser = getJsonParser(webRequest);
        Object targetClass = getTargetClass(parameter);
        StdDeserializer customDeserializer = (StdDeserializer) deserializeMap.get(targetClass);
        return  customDeserializer.deserialize(parser,null);
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
            RequestDTO dtoType = AnnotationUtils.getAnnotation(ann, RequestDTO.class);
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
