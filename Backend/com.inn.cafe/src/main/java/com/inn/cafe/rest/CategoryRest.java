package com.inn.cafe.rest;


import com.inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Category api interface
@RequestMapping(path = "/category")
public interface CategoryRest {

// --------Add new category api
    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String,String> requestMap);

    // --------Get all category api
    @GetMapping(path = "/get")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue);

    @PostMapping(path = "/updateCategory")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true)Map<java.lang.String, java.lang.String> requestMap);



}
