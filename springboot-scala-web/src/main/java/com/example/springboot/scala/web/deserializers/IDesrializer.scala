package com.example.springboot.scala.web.deserializers

import com.fasterxml.jackson.core.JsonParser

trait IDesrializer {

  def deserialize(jsonParser: JsonParser): Any
}
