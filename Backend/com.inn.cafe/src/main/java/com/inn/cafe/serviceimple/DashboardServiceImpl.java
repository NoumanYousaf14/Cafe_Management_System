package com.inn.cafe.serviceimple;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {


    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BillDao billDao;



//    service of get details
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
           Map<String ,Object> map=new HashMap<>();
            map.put("Category ",categoryDao.count());
            map.put("Product ",productDao.count());
            map.put("Bill ",billDao.count());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (Exception ex){

            ex.printStackTrace();
        }
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
