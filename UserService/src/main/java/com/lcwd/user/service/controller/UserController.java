package com.lcwd.user.service.controller;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        User savedUser = userService.saveUser(user);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    @GetMapping("users/{userId}")
    public ResponseEntity<User> getUserWithUserId(@PathVariable  String userId){
        User user = userService.getUserWithUserId(userId);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser(){

        List<User> allUsers = userService.getAllUser();

        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }
}
