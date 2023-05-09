package com.example.springboot.scala.web.serializers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

class UserCustomScalaSerializer extends ISerializer {
  override def serialize(src: Any,objectMapper:ObjectMapper): Any = {
       return objectMapper.valueToTree(src);
  }
}