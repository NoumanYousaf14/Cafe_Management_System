package com.inn.cafe.rest;


import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//-------user api interface
@RequestMapping(path = "/user")
public interface UserRest {

//    --------signup api
    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true)Map<String,String> requestMap);

//    --------login Api
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true)Map<String,String> requestMap);
    
//    Api to get All users
    @GetMapping(path = "/get")
    public  ResponseEntity<List<UserWrapper>> getAllUser();

//    APi to enable and disable user account
    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true)Map<java.lang.String, java.lang.String> requestMap);

    //    APi to check token
    @GetMapping(path="/checkToken")
    ResponseEntity<String> checkToken();

//    Api to change password
    @PostMapping(path = "/changePassword")
    ResponseEntity <String> changePassword(@RequestBody Map<String,String> requestMap);

    //    Api to Forgot password
    @PostMapping(path = "/forgotPassword")
    ResponseEntity <String> forgotPassword(@RequestBody Map<String,String> requestMap);





}


