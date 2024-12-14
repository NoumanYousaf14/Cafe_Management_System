package com.inn.cafe.service;


import com.inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

//  Category service interface
public interface CategoryService {
    //  abstract function  for addNewCategory
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);


    //  abstract function  for addNewCategory
    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    //  abstract function  for update category
    ResponseEntity<String> updateCategory(Map<String, String> requestMap);
}
