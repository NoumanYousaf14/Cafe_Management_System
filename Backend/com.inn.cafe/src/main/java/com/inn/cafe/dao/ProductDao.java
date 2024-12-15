package com.inn.cafe.dao;

import com.inn.cafe.POJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product,Integer> {

// abstract query function to get all product
    List<ProductWrapper> getAllProduct();

    // abstract query function update product status
    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status, @Param("id") int id);

//    abstract query function to get all product by category
    List<ProductWrapper> getByCategory(@Param("id")  Integer id);

//    abstract query function to get all product by category
    List<ProductWrapper> getProductById(@Param("id") Integer id);
}
