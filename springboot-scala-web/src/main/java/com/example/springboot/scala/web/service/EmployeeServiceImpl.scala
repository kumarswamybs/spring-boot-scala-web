package com.example.springboot.scala.web.service

import com.example.springboot.scala.web.dto.EmployeeScalaDto
import org.springframework.stereotype.Service

@Service
class EmployeeServiceImpl {

  def addEmployee(employee: EmployeeScalaDto): EmployeeScalaDto = {
    return employee;
  }

}
