package com.inn.cafe.rest;

import com.inn.cafe.PROJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);
    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProduct();
    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id);
    @PostMapping(path = "updateStatus")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);
    @GetMapping(path = "getByCategory/{id}")
    ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable("id") Integer id);
    @GetMapping(path = "/getById/{id}")
    ResponseEntity <ProductWrapper> getById(@PathVariable("id") Integer id);


}
