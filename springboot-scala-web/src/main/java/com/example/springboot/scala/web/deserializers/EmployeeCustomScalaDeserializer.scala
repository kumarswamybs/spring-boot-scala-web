package com.example.springboot.scala.web.deserializers

import com.example.springboot.scala.web.dto.{EmployeeScalaDto, UserScalaDto}
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode

class EmployeeCustomScalaDeserializer extends IDesrializer {
  override def deserialize(jsonParser: JsonParser): Any = {
    var node:JsonNode= jsonParser.getCodec.readTree(jsonParser)
    var firstName =  node.get("firstName").asText();
    var lastName = node.get("lastName").asText();
    var email = node.get("email").asText();
    var password = node.get("password").asText();
    return  new EmployeeScalaDto(firstName,lastName,email, password)
   }
}
