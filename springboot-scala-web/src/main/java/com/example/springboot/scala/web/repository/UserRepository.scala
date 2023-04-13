package com.example.springboot.scala.web.repository

import com.example.springboot.scala.web.entity.User
import org.springframework.data.jpa.repository.JpaRepository

trait UserRepository extends JpaRepository[User,Integer]{

}