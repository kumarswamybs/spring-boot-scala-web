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
    @Autowired
    private RequestMappingHandlerAdapter adapter;

    private CustomRequestResponseBodyMethodProcessor customRequestResponseBodyMethodProcessor;

    private ApplicationContext applicationContext;

    @Autowired
    public WebMvcConfig(ApplicationContext applicationContext, CustomRequestResponseBodyMethodProcessor customRequestResponseBodyMethodProcessor) {
        this.applicationContext = applicationContext;
        this.customRequestResponseBodyMethodProcessor = customRequestResponseBodyMethodProcessor;
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
       argumentResolvers.add(customRequestResponseBodyMethodProcessor);
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
                handlersNewList.add(customRequestResponseBodyMethodProcessor);
            }
        }
        return handlersNewList;
    }


}
