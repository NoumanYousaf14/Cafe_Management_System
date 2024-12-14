package com.inn.cafe.serviceimple;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.untils.CafeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//  Category service class which implements Category Service interface
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

//-----------service of Add new Category
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap,false)){
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Category is Added Succesffully",HttpStatus.OK);

                }

            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


    }


    //    function to validate the category map
    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id")&& validateId){
                return true;
            }
            else return !validateId;
        }
        return false;
    }

    //function to get category from map
    private Category getCategoryFromMap(Map<String,String> requestMap, Boolean isAdd){
        Category category=new Category();
        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return  category;
    }



    //-----------service of get all Category
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
       try{

           if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
               log.info("Inside IF");
               return new ResponseEntity<List<Category>> (categoryDao.getAllCategory(),HttpStatus.OK);
           }
           return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);

       }catch (Exception ex)
       {
           ex.printStackTrace();
       }
       return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //-----------service of update Category
    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap,true)){
                    Optional<Category> optional=categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()){
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return CafeUtils.getResponseEntity("Category is updated Successfully",HttpStatus.OK);
                    }
                    else {
                        return CafeUtils.getResponseEntity("Category id does not exist",HttpStatus.OK);
                    }
                   
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }


}

