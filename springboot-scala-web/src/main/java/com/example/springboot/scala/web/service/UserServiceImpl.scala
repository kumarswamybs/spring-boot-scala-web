package com.example.springboot.scala.web.service

import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.example.springboot.scala.web.dto.UserScalaDto
import com.example.springboot.scala.web.entity.User
import com.example.springboot.scala.web.repository.UserRepository

import java.util
import java.util.Optional

@Service
class UserServiceImpl
{
  @Autowired
  var userRepository:UserRepository = _;

  @Autowired
  var modelMapper:ModelMapper = _;

  def createUser(userScalaDto: UserScalaDto): User = {
    var user: User = new User();
    user.firstName = userScalaDto.firstName
    user.lastName = userScalaDto.lastName;
    return user;
  }

   def getUserById(id: Integer): UserScalaDto = {
    var user:Optional[User]  = userRepository.findById(id);
    var response: UserScalaDto =  modelMapper.map(user.get(),classOf[UserScalaDto]);
    return response;
  }


   def updateUser(userDTO: UserScalaDto): UserScalaDto = {
    var user:User =  userRepository.findById(null).get();
   /* user.firstName = userDTO.getFirstName;
    user.lastName = userDTO.getLastName;
    user.email = userDTO.getEmail;*/
     userRepository.save(user);
    return userDTO;
  }

   def getAllUsers: util.List[UserScalaDto] = {
    var users:util.List[User] = userRepository.findAll();
    var usersDto:util.List[UserScalaDto] = modelMapper.map(users,classOf[util.List[UserScalaDto]]);
    return usersDto;
  }

   def deleteUserById(id: Integer):String = {
    userRepository.deleteById(id);
    return "deleted"
  }

}