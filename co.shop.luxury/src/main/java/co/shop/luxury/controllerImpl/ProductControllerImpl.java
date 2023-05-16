package co.shop.luxury.controllerImpl;

import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.controller.ProductController;
import co.shop.luxury.service.ProductService;
import co.shop.luxury.utils.JoyeriaUtils;
import co.shop.luxury.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductControllerImpl implements ProductController {

    @Autowired
    ProductService productService;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            return productService.addNewProduct(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try{
            return productService.getAllProducts();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            return productService.updateProduct(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            return productService.deleteProduct(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            return productService.updateStatus(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try{
            productService.getByCategory(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try{
            return productService.getProductById(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
