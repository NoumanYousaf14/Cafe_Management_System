package com.inn.cafe.dao;

import com.inn.cafe.POJO.User;
import com.inn.cafe.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//---Data access Object or Repository for the User data
@Repository
public interface UserDao extends JpaRepository<User,Integer> {

// abstract query function to find user by email
    User findByEmailId(@Param("email") String email);


// abstract query function to get all user
    List<UserWrapper> getAllUser();

    // abstract query function to get all admin
    List<String> getAllAdmin();

    // abstract query function to update the user status
    @Transactional
    @Modifying
    void updateStatus(@Param("status") String status,@Param("id") Integer id);

    // abstract query function to get the user by mail
    User findByEmail(String email);

}
