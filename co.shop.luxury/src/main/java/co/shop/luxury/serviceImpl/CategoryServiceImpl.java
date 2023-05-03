package co.shop.luxury.serviceImpl;

import co.shop.luxury.JsonWebToken.JwtFilter;
import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.Category;
import co.shop.luxury.repository.CategoryRepository;
import co.shop.luxury.service.CategoryService;
import co.shop.luxury.utils.JoyeriaUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryRepository.save(getCategoryFromMap(requestMap, false));
                    return JoyeriaUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
                }

            }else{
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validated) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validated){
                return true;
            }else if (!validated){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap (Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside if");
                return new ResponseEntity<>(categoryRepository.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryRepository.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()) {
                        categoryRepository.save(getCategoryFromMap(requestMap, true));
                        return JoyeriaUtils.getResponseEntity("Categoria actualizada correctamente", HttpStatus.OK);
                    }else{
                        return JoyeriaUtils.getResponseEntity("No existe categoria con ese id", HttpStatus.OK);
                    }
                }
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                JoyeriaUtils.getResponseEntity(JoyeriaConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
