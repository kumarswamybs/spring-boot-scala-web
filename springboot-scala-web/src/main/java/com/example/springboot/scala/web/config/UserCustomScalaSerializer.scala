package com.example.springboot.scala.web.config

import ch.qos.logback.classic.pattern.ClassOfCallerConverter
import com.example.springboot.scala.web.dto.UserScalaDto
import com.example.springboot.scala.web.entity.User
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class UserCustomScalaSerializer  {
  def serialize(src: Object): UserScalaDto ={
    var user: User = src.asInstanceOf[User]
    return new UserScalaDto(user.getFirstName, user.getLastName);
  }
}