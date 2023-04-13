package com.example.springboot.scala.web.entity

import javax.persistence.{Entity, GeneratedValue, Id, Table}
import scala.beans.BeanProperty

@Entity
@Table(name = "phones")
class Phone {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Integer = _

  @BeanProperty
  var number:String = _

}
