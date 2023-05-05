package co.shop.luxury.service;


import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OrderService {

    ResponseEntity<String> generate (Map<String, Object> requestMap);
}
