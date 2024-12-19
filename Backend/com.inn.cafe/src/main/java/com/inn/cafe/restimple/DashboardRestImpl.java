package com.inn.cafe.restimple;

import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.rest.DashboardRest;
import com.inn.cafe.service.BillService;
import com.inn.cafe.service.DashboardService;
import com.inn.cafe.untils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImpl implements DashboardRest {

    @Autowired
    DashboardService dashboardService;
//    implementation of details api
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
            return dashboardService.getCount();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", CafeConstants.SOMETHING_WENT_WRONG));
    }
}
