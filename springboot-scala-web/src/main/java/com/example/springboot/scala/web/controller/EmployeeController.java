package com.example.springboot.scala.web.controller;

import com.example.springboot.scala.web.annotations.Deserializer;
import com.example.springboot.scala.web.annotations.Serializer;
import com.example.springboot.scala.web.deserializers.EmployeeCustomScalaDeserializer;
import com.example.springboot.scala.web.serializers.EmployeeCustomScalaSerializer;
import com.example.springboot.scala.web.serializers.UserCustomScalaSerializer;
import com.example.springboot.scala.web.service.EmployeeServiceImpl;
import com.example.springboot.scala.web.dto.EmployeeScalaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/employees")
@RestController
public class EmployeeController {

    @Autowired
    EmployeeServiceImpl employeeService;

    @PostMapping
    @Serializer(EmployeeCustomScalaSerializer.class)
    public EmployeeScalaDto addEmployee(@Deserializer(EmployeeCustomScalaDeserializer.class) EmployeeScalaDto employeeScalaDto) {
        EmployeeScalaDto response =  employeeService.addEmployee(employeeScalaDto);
        return response;
    }
}
