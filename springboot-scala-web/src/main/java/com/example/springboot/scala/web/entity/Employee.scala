package com.example.springboot.scala.web.entity

import java.util
import javax.persistence.{CascadeType, Entity, GeneratedValue, Id, JoinColumn, OneToMany, Table}
import scala.beans.BeanProperty

@Entity
@Table(name = "employees")
class  Employee {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Integer = _

  @BeanProperty
  var firstName: String = _

  @BeanProperty
  var lastName: String = _

  @BeanProperty
  var email: String = _

  @BeanProperty
  var password: String = _

  @BeanProperty
  var address: String = _

  @BeanProperty
  @OneToMany(cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  var phones: util.List[Phone] = _

}
