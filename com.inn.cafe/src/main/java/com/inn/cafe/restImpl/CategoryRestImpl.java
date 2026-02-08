package com.inn.cafe.restImpl;

import com.inn.cafe.PROJO.Category;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.rest.CategoryRest;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestImpl implements CategoryRest {
    @Autowired
    CategoryService categoryService;

    @Override
    public ResponseEntity<String> addCategory(Map<String, String> requestMap) {
        try {
            return categoryService.addNewCategory(requestMap);
        }
        catch (Exception e) {
        e.printStackTrace();}
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filter) {
       try {
          return categoryService.getAllCategory(filter);
       }
       catch (Exception e) {
           e.printStackTrace();
       }
      return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            return categoryService.update(requestMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
