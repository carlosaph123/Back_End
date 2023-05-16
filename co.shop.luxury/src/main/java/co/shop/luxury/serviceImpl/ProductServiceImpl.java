package co.shop.luxury.serviceImpl;

import co.shop.luxury.JsonWebToken.JwtFilter;
import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.Category;
import co.shop.luxury.model.Product;
import co.shop.luxury.repository.CategoryRepository;
import co.shop.luxury.repository.ProductRepository;
import co.shop.luxury.service.ProductService;
import co.shop.luxury.utils.JoyeriaUtils;
import co.shop.luxury.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.catalog.CatalogFeatures;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    productRepository.save(getProductFromMap(requestMap, false));
                    return JoyeriaUtils.getResponseEntity("Producto agregado correctamente", HttpStatus.OK);
                }
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }else{
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();

        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));

        }else{
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name") && requestMap.containsKey("description")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
        return false;
    }


    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try{
            return new ResponseEntity<>(productRepository.getAllProducts(), HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, true)){
                    Optional<Product> optional= productRepository.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productRepository.save(product);
                        return JoyeriaUtils.getResponseEntity("Producto actualizado correctamente", HttpStatus.OK);
                    }else{
                        return JoyeriaUtils.getResponseEntity("Producto no existe", HttpStatus.OK);
                    }
                }else{
                    return JoyeriaUtils.getResponseEntity(JoyeriaConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else{
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional <Product> optional = productRepository.findById(id);
                if(!optional.isEmpty()){
                    productRepository.deleteById(id);
                    return JoyeriaUtils.getResponseEntity("Producto eliminado correctamente", HttpStatus.OK);
                }else{
                    return JoyeriaUtils.getResponseEntity("Producto no existe", HttpStatus.OK);
                }
            }else{
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional <Product> optional = productRepository.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    productRepository.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return JoyeriaUtils.getResponseEntity("Estado del producto actualizado correctamente", HttpStatus.OK);
                }else{
                    return JoyeriaUtils.getResponseEntity("Producto no existe", HttpStatus.OK);
                }
            }else{
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
