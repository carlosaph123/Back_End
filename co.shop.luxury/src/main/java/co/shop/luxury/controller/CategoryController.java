package co.shop.luxury.controller;

import co.shop.luxury.model.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping (path = "/category")
public interface CategoryController {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam (required=false) String filterValue);

    @PostMapping(path = "/update")
    ResponseEntity<String> updateCategory(@RequestBody Map<String, String> requestMap);

}
