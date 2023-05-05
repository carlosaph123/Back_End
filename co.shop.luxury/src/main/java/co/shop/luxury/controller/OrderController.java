package co.shop.luxury.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/order")
public interface OrderController {

    @PostMapping(path = "/generate")
    ResponseEntity<String> generate (@RequestBody Map<String, Object> requestMap);

}
