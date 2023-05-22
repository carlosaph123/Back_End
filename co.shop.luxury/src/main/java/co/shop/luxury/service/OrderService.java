package co.shop.luxury.service;


import co.shop.luxury.model.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {

    ResponseEntity<String> generate (Map<String, Object> requestMap);

    ResponseEntity<List<Order>> getOrders();
}
