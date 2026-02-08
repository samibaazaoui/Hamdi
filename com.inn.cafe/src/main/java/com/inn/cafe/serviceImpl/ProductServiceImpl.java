package com.inn.cafe.serviceImpl;

import com.inn.cafe.PROJO.Category;
import com.inn.cafe.PROJO.Product;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
           if (jwtFilter.isAdmin()){
              if (validateProductMap(requestMap,false)){
                 productDao.save(getProductMap(requestMap,false));
                  return CafeUtils.getResponseEntity("successfully created", HttpStatus.OK);

              }
              return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
           }
           else {
               return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

           }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductMap(Map<String, String> requestMap, boolean b) {
        Product product = new Product();
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        if (b) {
            product.setId(Integer.parseInt(requestMap.get("id")));
        }
        else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;

    }

    /*private boolean validateProductMap(Map<String, String> requestMap,boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
               return true;
            }
            else{
                return false;
            }
        }
        return false;
    }*/
private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {

    if (requestMap.containsKey("name")
            && requestMap.containsKey("description")
            && requestMap.containsKey("price")
            && requestMap.containsKey("categoryId")) {

        if (validateId) {
            return requestMap.containsKey("id");
        }

        return true;
    }

    return false;
  }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try{
           return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
      try {
          if (jwtFilter.isAdmin()){
              Optional optional = productDao.findById(id);
              if (optional.isPresent()) {
                 productDao.deleteById(id);
                 return CafeUtils.getResponseEntity("successfully deleted", HttpStatus.OK);
              }
              return CafeUtils.getResponseEntity("id Does not exist", HttpStatus.NOT_FOUND);
          }
          else {
              return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
          }

      }
      catch (Exception e) {
          e.printStackTrace();
      }
      return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
              Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
              if (optional.isPresent()) {
                 productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                 return CafeUtils.getResponseEntity("successfully updated", HttpStatus.OK);
              }
              return CafeUtils.getResponseEntity("product id doest not exist", HttpStatus.NOT_FOUND);
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
    try{
           return new ResponseEntity<List<ProductWrapper>>(productDao.getProductByCategory(id),HttpStatus.OK);
    }
    catch (Exception e) {
        e.printStackTrace();
    }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

