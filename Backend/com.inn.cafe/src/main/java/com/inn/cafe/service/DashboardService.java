package com.inn.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashboardService {

//     abstract function  for get details
    ResponseEntity<Map<String, Object>> getCount();
}
