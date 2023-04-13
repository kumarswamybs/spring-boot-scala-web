package com.example.springboot.scala.web.dto

import scala.beans.BeanProperty

class EmployeeScalaDto {

  def this(email: String, password: String) {
    this();
    this.email = email;
    this.password = password;
  }

  @BeanProperty
  var firstName: String = _;

  @BeanProperty
  var lastName: String = _;

  @BeanProperty
  var email: String = _;

  @BeanProperty
  var password: String = _;
}

