package com.inn.cafe.rest;

import com.inn.cafe.POJO.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Category api interface
@RequestMapping(path = "/bill")
public interface BillRest {
    // --------generate report api
    @PostMapping(path = "/generateReport")
    ResponseEntity<String> generateReport(@RequestBody Map<String,Object> requestMap);

    // --------get bill as admin and users api
    @GetMapping(path="/getBills")
    ResponseEntity<List<Bill>> getBills();

    // --------get bill as admin and users api
    @PostMapping(path="/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String,Object> requestMap);

    // --------delete bill api
    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id);


}
