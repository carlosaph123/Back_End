package co.shop.luxury.controllerImpl;

import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.controller.OrderController;
import co.shop.luxury.model.Order;
import co.shop.luxury.service.OrderService;
import co.shop.luxury.utils.JoyeriaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class OrderControllerImpl implements OrderController {

    @Autowired
    OrderService orderService;

    @Override
    public ResponseEntity<String> generate(Map<String, Object> requestMap) {
        try{
            return orderService.generate(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        try{
            return orderService.getOrders();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
