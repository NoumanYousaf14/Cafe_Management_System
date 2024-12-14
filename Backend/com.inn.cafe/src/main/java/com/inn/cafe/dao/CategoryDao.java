package com.inn.cafe.dao;

import com.inn.cafe.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


//---Data access Object or Repository for the category data
@Repository
public interface CategoryDao extends JpaRepository<Category,Integer> {

//     abstract query function to get all categories
    List<Category> getAllCategory();
}
