package com.security.oauth2.controller;


import com.security.oauth2.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    @Autowired
    UserManager userManager;


    @GetMapping("{id}")

    public ResponseEntity<?> getUserById(@PathVariable String id){
        return new ResponseEntity<>(userManager.getUserById(id), HttpStatus.OK);
    }
}
