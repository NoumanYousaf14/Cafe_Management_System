package com.inn.cafe.serviceimple;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtils;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.untils.CafeUtils;
//import lombok.extern.slf4j.Slf4j;
import com.inn.cafe.untils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


//  User service class which implements Category user interface
//@Slf4j
@Service
public class UserServiceImpl implements UserService {


    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private EmailUtils emailUtils;

//----------service of signup
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp: {}", requestMap);
        try {
            if (validateSignUp(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {

                    userDao.save(getUserFromMap(requestMap));
                    return new ResponseEntity<>("User successfully signed up.", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exist.", HttpStatus.BAD_REQUEST);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //    ----validate the signup values
    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

//---get user data from the map
    private User getUserFromMap(Map<String,String> requestMap){
        User user=new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }



    //    -----------service of login
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login!");
        try{
            log.info("Authenticating user: {}", requestMap.get("email"));
            Authentication auth=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            log.info("Authentication result: {}", auth.isAuthenticated());
            if (auth.isAuthenticated()){
                if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtils.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())+"\"}",HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait For Admin Approval"+"\"}", HttpStatus.BAD_REQUEST);
                }

            }

        }catch (Exception ex){
            log.error("Error during authentication: {}", ex.getMessage());

        }

        return new ResponseEntity<String>("{\"message\":\""+"Bad Credentials."+"\"}", HttpStatus.BAD_REQUEST);
    }


    //    -----------service of get all user
    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        log.info("Checking if user has admin privileges.");
        log.info("Authentication context: {}", SecurityContextHolder.getContext().getAuthentication());

        try{
            if (jwtFilter.isAdmin()){
                List<UserWrapper> users=userDao.getAllUser();
                System.out.println(users);
                log.info("User role: {}", jwtFilter.isAdmin() ? "admin" : "not admin");
                return new ResponseEntity<>(users,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //      -----------service to update user
    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){
              Optional<User> optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
              if(!optional.isEmpty()){
                  userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                  sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                  return CafeUtils.getResponseEntity("User updated successfully ",HttpStatus.OK);

              }else {
                  return CafeUtils.getResponseEntity("User id does not exist",HttpStatus.OK);
              }
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //    method to send the email
    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status!=null&&status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER:- "+user+"\n is approved by \n ADMIN:- "+jwtFilter.getCurrentUser(),allAdmin);
        }else{
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:- "+user+"\n is disabled by \n ADMIN:- "+jwtFilter.getCurrentUser(),allAdmin);
        }


    }



    //      -----------service to check token
    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true",HttpStatus.OK);

    }


//    -----------service to change password
    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObj=userDao.findByEmail(jwtFilter.getCurrentUser());
            if (userObj != null){
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return CafeUtils.getResponseEntity("Password Updated Successfully",HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity("Incorrect old Password",HttpStatus.BAD_REQUEST);

            }


            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //    -----------service to forgot password
    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
       try{
           User user=userDao.findByEmail(requestMap.get("email"));
           if (!Objects.isNull(user)&& !Strings.isNullOrEmpty(user.getEmail())){
               emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System",user.getPassword());
           }
           return CafeUtils.getResponseEntity("Check your mail for Credentials",HttpStatus.OK);

       }catch (Exception ex){
           ex.printStackTrace();
       }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
