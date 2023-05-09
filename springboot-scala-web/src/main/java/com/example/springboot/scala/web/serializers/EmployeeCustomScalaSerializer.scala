package com.example.springboot.scala.web.serializers

import com.example.springboot.scala.web.dto.EmployeeScalaDto
import com.fasterxml.jackson.databind.ObjectMapper

class EmployeeCustomScalaSerializer extends ISerializer {
  override def serialize(src: Any,objectMapper:ObjectMapper): Any = {
    var employeeScalaDto: EmployeeScalaDto = src.asInstanceOf[EmployeeScalaDto]
    return new EmployeeScalaDto("firstName","lastName","test@gmail.com","test");
  }
}
