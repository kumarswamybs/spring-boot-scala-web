package com.example.springboot.scala.web.dto

import scala.beans.BeanProperty

class UserScalaDto {

  def this(fName:String,lName:String) {
    this();
    firstName = fName;
    lastName = lName;
  }

  @BeanProperty
  var firstName:String =_;

  @BeanProperty
  var lastName:String =_;
}
