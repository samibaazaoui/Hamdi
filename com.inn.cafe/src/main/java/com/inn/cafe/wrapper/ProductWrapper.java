package com.inn.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductWrapper {
    Integer id;
    String name;
    String description;
    Integer price;

    String status;
    Integer categoryId;
    String categoryName;
    public ProductWrapper() {

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
