package co.shop.luxury.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class JoyeriaUtils {

    private JoyeriaUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("\"Message\":\" "+responseMessage, httpStatus);
    }
}