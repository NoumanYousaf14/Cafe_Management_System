package com.inn.cafe.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductWrapper {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("status")
    private String status;

    @JsonProperty("categoryId")
    private Integer categoryId;

    @JsonProperty("categoryName")
    private  String categoryName;

    public ProductWrapper(){

    }

    public ProductWrapper(Integer id, String name, String description, Integer price, String status, Integer categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    public ProductWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;

    }
    public ProductWrapper(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;

    }
}
