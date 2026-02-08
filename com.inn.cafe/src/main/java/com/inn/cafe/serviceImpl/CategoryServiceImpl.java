package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.PROJO.Category;
import com.inn.cafe.PROJO.User;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap,false)){
                   categoryDao.save(getCategoryFromRequestMap(requestMap,false));
                   return CafeUtils.getResponseEntity("added successfuly",HttpStatus.OK);
                }
            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private boolean validateCategoryMap(Map<String, String> requestMap,boolean validateId) {
        if(requestMap.containsKey("name")&&validateId){
            return true;
        }
        else if(!validateId){
            return true;
        }
        return false;
    }
    private Category getCategoryFromRequestMap(Map<String, String> requestMap,Boolean isAdmin) {
        Category category = new Category();
        if(isAdmin){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filter) {
        try{
            if(!Strings.isNullOrEmpty(filter) &&filter.equalsIgnoreCase("true")){
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Category> option =  categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!option.isEmpty()){
                    categoryDao.update(requestMap.get("name"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("success updated", HttpStatus.OK);
                }
                else{
                    return CafeUtils.getResponseEntity("Category doesnt exist ", HttpStatus.OK);
                }
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
}
