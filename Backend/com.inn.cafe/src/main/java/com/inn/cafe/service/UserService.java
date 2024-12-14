package com.inn.cafe.service;

import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


//  user service interface
@Service
public interface UserService {
//  abstract function  for signup

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    //  abstract function  for login
    ResponseEntity<String> login(Map<String, String> requestMap);

    //  abstract function  for get user
    ResponseEntity<List<UserWrapper>> getAllUser();

    //  abstract function  to update user
    ResponseEntity<String> update(Map<String,String> requestMap);


    //  abstract function  to check token
    ResponseEntity<String> checkToken();

    //  abstract function  to check token
    ResponseEntity<String> changePassword(Map<String, String> requestMap);


    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

}
