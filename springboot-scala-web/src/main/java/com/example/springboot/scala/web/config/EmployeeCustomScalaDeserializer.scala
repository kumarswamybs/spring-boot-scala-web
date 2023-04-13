package com.example.springboot.scala.web.config

import com.example.springboot.scala.web.dto.EmployeeScalaDto
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class EmployeeCustomScalaDeserializer extends StdDeserializer[EmployeeScalaDto](classOf[EmployeeScalaDto]) {
  override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): EmployeeScalaDto = {
    val node:JsonNode = jsonParser.getCodec.readTree(jsonParser)
    val email =  node.get("email").asText();
    val password = node.get("password").asText();
    return new EmployeeScalaDto(email,password);
  }
}
