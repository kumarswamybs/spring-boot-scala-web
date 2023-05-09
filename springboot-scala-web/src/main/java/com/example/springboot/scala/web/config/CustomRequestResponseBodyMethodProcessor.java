package com.example.springboot.scala.web.config;

import com.example.springboot.scala.web.annotations.Deserializer;
import com.example.springboot.scala.web.annotations.Serializer;
import com.example.springboot.scala.web.deserializers.IDesrializer;
import com.example.springboot.scala.web.serializers.ISerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.*;
@Component
public class CustomRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    private ObjectMapper objectMapper;

    @Autowired
    public CustomRequestResponseBodyMethodProcessor(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,ObjectMapper objectMapper) {
        super(Collections.singletonList(mappingJackson2HttpMessageConverter));
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Deserializer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonParser parser = getJsonParser(webRequest);
        Deserializer deserializerAnnotation = parameter.getParameterAnnotation(Deserializer.class);
        // Get the deserializer class specified in the annotation
        Class<?> deserializerClass = deserializerAnnotation.value();
        // Create a new instance of the deserializer using the class
        IDesrializer deserializer = (IDesrializer) deserializerClass.newInstance();
        return  deserializer.deserialize(parser);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (returnType.getMethodAnnotation(Serializer.class) != null || super.supportsReturnType(returnType));
    }

    @Override
    public void handleReturnValue(Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {
        Serializer serializerAnnotation = returnType.getMethodAnnotation(Serializer.class);
        // Get the class specified in the @Serializer annotation
        Class<?> serializerClass = serializerAnnotation.value();
        // Create an instance of the specified class
        try {
            ISerializer customSerializer = (ISerializer)serializerClass.newInstance();
            returnValue = customSerializer.serialize(returnValue,objectMapper);
            super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonParser getJsonParser(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
        EmptyBodyCheckingHttpInputMessage message = new EmptyBodyCheckingHttpInputMessage(inputMessage);
        InputStream inputStream = StreamUtils.nonClosing(message.getBody());
        return mappingJackson2HttpMessageConverter.getObjectMapper().getFactory().createParser(inputStream);
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
