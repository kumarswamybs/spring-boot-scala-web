package com.example.springboot.scala.web.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.example.springboot.scala.web.entity.Employee

trait EmployeeRepository extends JpaRepository[Employee,Integer]{

}
