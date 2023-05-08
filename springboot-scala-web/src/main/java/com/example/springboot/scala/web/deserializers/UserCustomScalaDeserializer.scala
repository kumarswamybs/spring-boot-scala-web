package com.example.springboot.scala.web.deserializers

import com.example.springboot.scala.web.dto.UserScalaDto
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode

class UserCustomScalaDeserializer extends IDesrializer {
  override def deserialize(jsonParser: JsonParser): Any = {
    var node:JsonNode= jsonParser.getCodec.readTree(jsonParser)
    var firstName =  node.get("firstName").asText();
    var lastName = node.get("lastName").asText();
    return new UserScalaDto(firstName, lastName)
  }
}
