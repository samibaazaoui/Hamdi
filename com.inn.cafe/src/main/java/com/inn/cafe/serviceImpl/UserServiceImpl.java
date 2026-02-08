package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.PROJO.User;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.jwt.CustomUserDetailsService;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.jwt.JwtUtils;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired

    CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils  jwtUtils;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
    log.info("Sign Up", requestMap);
    try {
        if (validateSignUpMap(requestMap)) {
            User user = userDao.findByEmailId(requestMap.get("email"));
            if (Objects.isNull(user)) {
                userDao.save(getUserFromMap(requestMap));
                return CafeUtils.getResponseEntity("success registred", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Email already exist", HttpStatus.BAD_REQUEST);
            }
        } else {
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }
    }
    catch (Exception e) {
        e.printStackTrace();
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
 }
    private boolean validateSignUpMap(Map<String,String> requestMap){
        if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;

    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
      log.info(" Inside Login ", requestMap);
      try {
          Authentication auth = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
          );
          if(auth.isAuthenticated()){
              if (customUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {

                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtils.generateToken(customUserDetailsService.getUserDetail().getEmail()
                            ,customUserDetailsService.getUserDetail().getRole())+"\"}",
                    HttpStatus.OK);
              }
              else {
                  return new ResponseEntity<String>("{\"message\":\"You are not allowed to login wait for admin\"}", HttpStatus.BAD_REQUEST);
              }
          }
      }
      catch (Exception e) {
          e.printStackTrace();}
      return new ResponseEntity<String>("{\"message\":\"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
         if(jwtFilter.isAdmin()){
            return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
         }
         else {
           return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
         }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<UserWrapper>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
             Optional<User> option =  userDao.findById(Integer.parseInt(requestMap.get("id")));
             if(!option.isEmpty()){
                userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                sendMailToAllAdmin(requestMap.get("status"),option.get().getEmail(),userDao.getAllAdmin());
                return CafeUtils.getResponseEntity("success updated", HttpStatus.OK);
             }
             else{
                return CafeUtils.getResponseEntity("User doesnt exist ", HttpStatus.OK);
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
    private void sendMailToAllAdmin(String status, String user, List<String> alladmin) {
       alladmin.remove(jwtFilter.getCurrentUser());
       if(status!=null && status.equalsIgnoreCase("true")){
          emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approuved","USER:-"+user + "\n is approuved by \nADMIN:-"+jwtFilter.getCurrentUser(),alladmin);
       }
       else{
           emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:-"+user + "\n is disabled by \nADMIN:-"+jwtFilter.getCurrentUser(),alladmin);

       }
    }

    @Override
    public ResponseEntity<String> checkToken() {
       return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
           User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
           if (!userObj.equals(null)) {
               if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                   userObj.setPassword(requestMap.get("newPassword"));
                   userDao.save(userObj);
                   return CafeUtils.getResponseEntity("success updated", HttpStatus.OK);

               }
               return CafeUtils.getResponseEntity("Incorrect password", HttpStatus.BAD_REQUEST);

           }
           return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
       try {
          User user = userDao.findByEmail(requestMap.get("email"));
          if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){
              emailUtils.forgetMail(user.getEmail(),"Credentials by Cafe Managment System",user.getPassword());
             return CafeUtils.getResponseEntity("Check your mail for Credentials", HttpStatus.OK);
          }
       }
       catch (Exception e) {
           e.printStackTrace();
       }
       return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
