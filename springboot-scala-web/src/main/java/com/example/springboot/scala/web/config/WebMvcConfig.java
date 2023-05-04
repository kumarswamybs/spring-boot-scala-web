package com.example.springboot.scala.web.config;

import com.appNgeek.dto_entity_auto_rest_api.convertor.handler.ResponseEntityToDTOHandlerMethodArgumentResolver;
import com.example.springboot.scala.web.dto.EmployeeScalaDto;
import com.example.springboot.scala.web.dto.UserScalaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    MappingJackson2HttpMessageConverter defaultOne;

    @Autowired
    private RequestMappingHandlerAdapter adapter;


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public WebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

  /*  @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jacksonMessageConverter = null;
        for (HttpMessageConverter<?> converter : converters) {
            System.out.println("here "+ converter);
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                jacksonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                break;
            }
        }
        if (jacksonMessageConverter == null) {
            jacksonMessageConverter = defaultOne;
            converters.add(jacksonMessageConverter);
        }

        ObjectMapper objectMapper = jacksonMessageConverter.getObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UserScalaDto.class, new UserCustomScalaDeserializer());
        module.addDeserializer(EmployeeScalaDto.class,new EmployeeCustomScalaDeserializer());
        objectMapper.registerModule(module);
    }*/

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        Map<Object,Object> desMap = new HashMap<>();
        desMap.put(UserScalaDto.class,new UserCustomScalaDeserializer());
        desMap.put(EmployeeScalaDto.class,new EmployeeCustomScalaDeserializer());
        Map<Object,Object> serMap = new HashMap<>();
        serMap.put(UserScalaDto.class,new UserCustomScalaSerializer());
        argumentResolvers.add(new DTOModelMapper(defaultOne,desMap,serMap,applicationContext));
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
                Map<Object,Object> serMap = new HashMap<>();
                serMap.put(UserScalaDto.class,new UserCustomScalaSerializer());
                List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
                messageConverters.add(new MappingJackson2HttpMessageConverter());
                com.appNgeek.dto_entity_auto_rest_api.convertor.handler.ResponseEntityToDTOHandlerMethodArgumentResolver decorator = new ResponseEntityToDTOHandlerMethodArgumentResolver(
                        messageConverters, applicationContext,serMap);
                handlersNewList.add(decorator);
            }

        }

        return handlersNewList;
    }


}
