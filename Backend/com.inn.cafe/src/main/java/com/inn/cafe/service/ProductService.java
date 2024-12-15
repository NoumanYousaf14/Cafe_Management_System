package com.inn.cafe.service;

import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    //  abstract function  for Add new product
     ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    //  abstract function  for get all product
    ResponseEntity<List<ProductWrapper>> getAllProduct();

    //  abstract function  for update product
    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    //  abstract function  for delete product
    ResponseEntity<String> deleteProduct(Integer id);

    //  abstract function  for update product Status
    ResponseEntity<String> updateStatus(Map<String, String> requestMap);

    //  abstract function  for get all product by category
    ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);

    //  abstract function  for get all product by id
    ResponseEntity<List<ProductWrapper>> getProductById(Integer id);
}
