package com.example.springboot.scala.web.serializers

import com.example.springboot.scala.web.dto.UserScalaDto
import com.example.springboot.scala.web.entity.User

class UserCustomScalaSerializer extends ISerializer {
  override def serialize(src: Any): Any = {
    var user: User = src.asInstanceOf[User]
    return new UserScalaDto(user.getFirstName, user.getLastName);
  }
}