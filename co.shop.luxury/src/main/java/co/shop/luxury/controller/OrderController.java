package co.shop.luxury.controller;

import co.shop.luxury.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/order")
public interface OrderController {

    @PostMapping(path = "/generate")
    ResponseEntity<String> generate (@RequestBody Map<String, Object> requestMap);

    @GetMapping(path = "/getOrders")
    ResponseEntity<List<Order>> getOrders();



}
