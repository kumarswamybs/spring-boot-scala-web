package com.example.springboot.scala.web.controller;

import com.example.springboot.scala.web.dto.EmployeeScalaDto;
import com.example.springboot.scala.web.service.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/employees")
@RestController
public class EmployeeController {

    @Autowired
    EmployeeServiceImpl employeeService;

    @PostMapping
    public ResponseEntity<EmployeeScalaDto> addEmployee(@RequestBody EmployeeScalaDto employeeScalaDto) {
        EmployeeScalaDto response =  employeeService.addEmployee(employeeScalaDto);
        return new ResponseEntity<EmployeeScalaDto>(response, HttpStatus.OK);
    }
}
