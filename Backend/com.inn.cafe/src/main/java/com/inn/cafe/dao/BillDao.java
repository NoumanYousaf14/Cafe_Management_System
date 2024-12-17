package com.inn.cafe.dao;

import com.inn.cafe.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDao extends JpaRepository<Bill,Integer> {

// abstract query function to get all bills
    List<Bill> getAllBills();
    // abstract query function to get all bills by username
    List<Bill> getAllBillsByUserName(@Param("username") String username);
}
