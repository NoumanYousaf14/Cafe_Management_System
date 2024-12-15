package com.inn.cafe.rest;


import com.inn.cafe.wrapper.ProductWrapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    // --------Add new product api
    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewProduct(@RequestBody(required = true) Map<String,String> requestMap);

    // --------get product api
    @GetMapping(path = "/get")
    public  ResponseEntity<List<ProductWrapper>> getAllProduct();

    // --------update product api
    @PostMapping(path = "/update")
    public ResponseEntity<String> updateProduct(@RequestBody Map<String,String> requestMap);

    // --------Delete product api
    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    // --------update Status product api
    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody Map<String,String> requestMap);

    // --------get product by category api
    @GetMapping(path = "/getByCategory/{id}")
    public  ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Integer id);

    @GetMapping(path = "/getById/{id}")
    public  ResponseEntity<List<ProductWrapper>> getProductById(@PathVariable Integer id);
}
