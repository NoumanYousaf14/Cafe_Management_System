package com.inn.cafe.service;

import com.inn.cafe.POJO.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {

    //  abstract function  for generate report
    ResponseEntity<String> generateReport(Map<String, Object> requestMap);

    //  abstract function  for get bills
    ResponseEntity<List<Bill>> getBills();

    //  abstract function  for get pdf of bills
    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);

    //  abstract function  delete bills
    ResponseEntity<String> deleteBill(Integer id);
}
