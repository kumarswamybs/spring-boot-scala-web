package com.example.springboot.scala.web.serializers

trait ISerializer {
  def serialize(src: Any): Any
}
