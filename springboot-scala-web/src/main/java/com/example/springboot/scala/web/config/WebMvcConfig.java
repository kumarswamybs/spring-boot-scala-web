package com.example.springboot.scala.web.config;

import com.example.springboot.scala.web.dto.EmployeeScalaDto;
import com.example.springboot.scala.web.dto.UserScalaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    MappingJackson2HttpMessageConverter defaultOne;

    @Override
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
    }

}
