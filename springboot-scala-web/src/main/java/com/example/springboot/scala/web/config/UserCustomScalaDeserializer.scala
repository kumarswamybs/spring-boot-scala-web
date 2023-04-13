package com.example.springboot.scala.web.config

import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.example.springboot.scala.web.dto.UserScalaDto
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode}

class UserCustomScalaDeserializer extends StdDeserializer[UserScalaDto](classOf[UserScalaDto]) {
  override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): UserScalaDto = {
    var node:JsonNode= jsonParser.getCodec.readTree(jsonParser)
    var firstName =  node.get("firstName").asText();
    var lastName = node.get("lastName").asText();
    return new UserScalaDto(firstName, lastName)
  }
}
