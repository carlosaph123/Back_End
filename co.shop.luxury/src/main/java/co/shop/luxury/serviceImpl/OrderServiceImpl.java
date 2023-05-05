package co.shop.luxury.serviceImpl;

import co.shop.luxury.JsonWebToken.JwtFilter;
import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.Order;
import co.shop.luxury.repository.OrderRepository;
import co.shop.luxury.service.OrderService;
import co.shop.luxury.utils.JoyeriaUtils;

import com.itextpdf.text.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generate(Map<String, Object> requestMap) {
        log.info("Inside generate ");
        try{
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                }else{
                    fileName = JoyeriaUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertOrder(requestMap);
                }

            }else{
                return JoyeriaUtils.getResponseEntity("No contiene toda la información requerida", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void insertOrder(Map<String, Object> requestMap) {
        try{
            Order order = new Order();
            order.setUuid((String)requestMap.get("uuid"));
            order.setName((String)requestMap.get("name"));
            order.setEmail((String)requestMap.get("email"));
            order.setContactNumber((String)requestMap.get("contactNumber"));
            order.setPayMethod((String)requestMap.get("payMethod"));
            order.setTotal((Integer)requestMap.get("total"));
            order.setProductDetails((String)requestMap.get("productDetails"));
            order.setCreatedBy(jwtFilter.getCurrentUser());
            orderRepository.save(order);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") && requestMap.containsKey("payMethod") &&
                requestMap.containsKey("productDetails") && requestMap.containsKey("total");
    }
}
