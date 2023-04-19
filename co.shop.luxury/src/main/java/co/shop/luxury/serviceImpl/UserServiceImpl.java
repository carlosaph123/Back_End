package co.shop.luxury.serviceImpl;

import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.User;
import co.shop.luxury.repository.UserRepository;
import co.shop.luxury.service.UserService;
import co.shop.luxury.utils.JoyeriaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}",requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User userEmail = userRepository.findByEmailId(requestMap.get("email"));
                User userId = getUserFromMap(requestMap);

                if (Objects.isNull(userEmail) &&  !userRepository.existsById(userId.getId())) {
                    userRepository.save(getUserFromMap(requestMap));
                    return JoyeriaUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return JoyeriaUtils.getResponseEntity("Email already exist", HttpStatus.BAD_REQUEST);
                }
            } else {
                return JoyeriaUtils.getResponseEntity(JoyeriaConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if(requestMap.containsKey("id")  && requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") && requestMap.containsKey("email") &&
                requestMap.containsKey("password")){
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setId(Integer.valueOf(requestMap.get("id")));
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}
