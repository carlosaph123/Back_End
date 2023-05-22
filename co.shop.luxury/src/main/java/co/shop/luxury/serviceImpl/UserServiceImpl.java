package co.shop.luxury.serviceImpl;

import co.shop.luxury.JsonWebToken.CustomerUserDetailsService;
import co.shop.luxury.JsonWebToken.JwtFilter;
import co.shop.luxury.JsonWebToken.JwtUtil;
import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.User;
import co.shop.luxury.repository.UserRepository;
import co.shop.luxury.service.UserService;
import co.shop.luxury.utils.EmailUtils;
import co.shop.luxury.utils.JoyeriaUtils;
import co.shop.luxury.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}",requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User userEmail = userRepository.findByEmailId(requestMap.get("email"));
                User userId = getUserFromMap(requestMap);

                if (Objects.isNull(userEmail) &&  !userRepository.existsById(userId.getId())) {
                    userRepository.save(getUserFromMap(requestMap));
                    return JoyeriaUtils.getResponseEntity("Registro Exitoso", HttpStatus.OK);
                } else {
                    return JoyeriaUtils.getResponseEntity("Correo electrónico o identificación ya existe", HttpStatus.BAD_REQUEST);
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

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));
            if(auth.isAuthenticated()){
                if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                            customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>("{\"message\":\""+"En espera de autorización por parte del Administrador"+"\"}", HttpStatus.BAD_REQUEST);
                                    }
            }
        }catch (Exception ex){
            log.error("{}", ex);
        }
        return new ResponseEntity<>("{\"message\":\""+"Usuario o contraseña incorrectos"+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userRepository.getAllUser(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> optional = userRepository.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    userRepository.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")) );
                    //sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userRepository.getAllAdmin());
                    return JoyeriaUtils.getResponseEntity("Actualización exitosa", HttpStatus.OK);
                }else{
                    return JoyeriaUtils.getResponseEntity("Usuario no existe", HttpStatus.OK);

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
    public ResponseEntity<String> checkToken() {
        return JoyeriaUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User user = userRepository.findByEmailId(jwtFilter.getCurrentUser());
            if(!user.equals(null)){
                if(user.getPassword().equals(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userRepository.save(user);
                    return JoyeriaUtils.getResponseEntity("Contraseña actualizada correctamente", HttpStatus.OK);
                }else{
                    return JoyeriaUtils.getResponseEntity("Contraseña actual es incorrecta", HttpStatus.BAD_REQUEST);
                }
            }
            return JoyeriaUtils.getResponseEntity("Usuario no existe", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:-"+ user + "\n is approved by \n ADMIN:-"+jwtFilter.getCurrentUser()+" .", allAdmin);
        }else{
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:-"+ user + "\n is disabled by \n ADMIN:-"+jwtFilter.getCurrentUser()+" .", allAdmin);
        }
    }



}
