package com.example.springboot.scala.web.serializers

import com.fasterxml.jackson.databind.ObjectMapper

trait ISerializer {
  def serialize(src: Any,objectMapper:ObjectMapper): Any
}
