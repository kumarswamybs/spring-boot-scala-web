package com.example.springboot.scala.web.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//    @Autowired
//    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private RequestMappingHandlerAdapter adapter;


    private CustomRequestResponseBodyMethodProcessor requestHandler;



    private ApplicationContext applicationContext;

    @Autowired
    public WebMvcConfig(ApplicationContext applicationContext, CustomRequestResponseBodyMethodProcessor requestHandler) {
        this.applicationContext = applicationContext;
        this.requestHandler = requestHandler;
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
       argumentResolvers.add(requestHandler);
    }


    @PostConstruct
    private void overwriteDefaultRequestResponseBodyMethodProcessor() {
        List<HandlerMethodReturnValueHandler> handlers = adapter.getReturnValueHandlers();
        handlers = decorateHandlers(handlers);
        adapter.setReturnValueHandlers(handlers);
    }

    private List<HandlerMethodReturnValueHandler> decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        List<HandlerMethodReturnValueHandler> handlersNewList = new ArrayList<>();

        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (!(handler instanceof RequestResponseBodyMethodProcessor)) {
                handlersNewList.add(handler);
            } else {
//                List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//                messageConverters.add(new MappingJackson2HttpMessageConverter());
//                ResponseHandler decorator = new ResponseHandler(
//                        messageConverters);
                handlersNewList.add(requestHandler);
            }

        }

        return handlersNewList;
    }


}
