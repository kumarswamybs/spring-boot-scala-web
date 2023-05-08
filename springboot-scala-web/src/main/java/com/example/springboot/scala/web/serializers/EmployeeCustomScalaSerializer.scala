package com.example.springboot.scala.web.serializers

import com.example.springboot.scala.web.dto.EmployeeScalaDto

class EmployeeCustomScalaSerializer extends ISerializer {
  override def serialize(src: Any): Any = {
    var employeeScalaDto: EmployeeScalaDto = src.asInstanceOf[EmployeeScalaDto]
    return new EmployeeScalaDto("firstName","lastName","test@gmail.com","test");
  }
}
