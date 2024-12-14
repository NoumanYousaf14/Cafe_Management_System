package com.inn.cafe.JWT;

import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.serviceimple.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;



@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomerUserDetailsService.class);


    @Autowired
    UserDao userDao;

    private com.inn.cafe.POJO.User userDetail;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loaduserbyemail : {}", username);
        userDetail = userDao.findByEmailId(username);
        if (!Objects.isNull(userDetail)) {
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
//            // Assuming roles are stored in a list or similar
//            Collection<String> roles = new ArrayList<>();
//            roles.add(userDetail.getRole());
//            return (UserDetails) new User(userDetail.getEmail(), userDetail.getPassword(), roles);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }


    public com.inn.cafe.POJO.User getUserDetail(){
        return userDetail;
    }
}
