package co.shop.luxury.controller;

import co.shop.luxury.model.Product;
import co.shop.luxury.wrapper.ProductWrapper;
import com.sun.mail.iap.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductController {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct (@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProducts();

    @PostMapping(path="/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping (path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus (@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getByCategory/{id}")
    ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Integer id);

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProductWrapper> getProductById (@PathVariable Integer id);

}
