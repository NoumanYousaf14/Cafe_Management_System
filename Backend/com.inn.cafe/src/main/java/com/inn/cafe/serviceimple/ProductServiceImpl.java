package com.inn.cafe.serviceimple;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.untils.CafeUtils;
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
    private JwtFilter jwtFilter;

    //-----------service of Add new product
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap){
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,false)){
                    productDao.save(getProductFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Product is Added Successfully",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //    Function to get the product from map
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category=new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product=new Product();
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }
        else{
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    //    Function to validate the product map
    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id")&& validateId){
                return true;
            }
            else return !validateId;
        }
        return false;
    }

    //-----------service of get all product
    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
      try{
          return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);

      }catch (Exception ex){
          ex.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //-----------service of update product
    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,true)){
                    Optional<Product> optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()){
                        Product product=getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity("Product is updated Successfully",HttpStatus.OK);

                    }else{
                        return CafeUtils.getResponseEntity("Product does not exist!",HttpStatus.OK);
                    }

                }else{
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //-----------service of delete product
    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if (jwtFilter.isAdmin()){
                Optional<Product> optional=productDao.findById(id);
                if (optional.isPresent()){
                   productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product is deleted Successfully",HttpStatus.OK);

                }else{
                    return CafeUtils.getResponseEntity("Product id does not exist!",HttpStatus.OK);
                }

            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //-----------service of update product status
    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                    Optional<Product> optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()){

                       productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                        return CafeUtils.getResponseEntity("Product status is updated Successfully",HttpStatus.OK);

                    }else{
                        return CafeUtils.getResponseEntity("Product id does not exist!",HttpStatus.OK);
                    }

            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //-----------service of get all product by category
    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getByCategory(id),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //-----------service of get all product by id
    @Override
    public ResponseEntity<List<ProductWrapper>> getProductById(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
