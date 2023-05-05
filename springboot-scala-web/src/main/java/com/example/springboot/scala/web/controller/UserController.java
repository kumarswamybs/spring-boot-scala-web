package com.example.springboot.scala.web.controller;

import com.example.springboot.scala.web.config.Deserializer;
import com.example.springboot.scala.web.config.ResponseBodyDTO;
import com.example.springboot.scala.web.config.UserCustomScalaDeserializer;
import com.example.springboot.scala.web.config.UserCustomScalaSerializer;
import com.example.springboot.scala.web.dto.UserScalaDto;
import com.example.springboot.scala.web.entity.User;
import com.example.springboot.scala.web.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping
    @ResponseBodyDTO(UserCustomScalaSerializer.class)
    public User addUser(@Deserializer(UserCustomScalaDeserializer.class) UserScalaDto scalObject){
        User response =  userService.createUser(scalObject);
        return response;
    }

    @GetMapping
    public ResponseEntity<List<UserScalaDto>> allUsers(){
        return new ResponseEntity<List<UserScalaDto>>(userService.getAllUsers(),HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserScalaDto> getUser(@PathVariable("userId") Integer userId) {
        return new ResponseEntity<UserScalaDto>(userService.getUserById(userId),HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserScalaDto> updateUser(@RequestBody UserScalaDto userDTO) {
        return new ResponseEntity<UserScalaDto>(userService.updateUser(userDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Integer userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
