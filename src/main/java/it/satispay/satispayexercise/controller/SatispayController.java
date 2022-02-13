package it.satispay.satispayexercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.satispay.satispayexercise.controller.dto.AuthenticationDto;
import it.satispay.satispayexercise.exception.AuthenticationException;
import it.satispay.satispayexercise.service.SatispayService;
import it.satispay.satispayexercise.service.response.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/satispay")
public class SatispayController {

    private final SatispayService satispayService;

    private final ObjectMapper objectMapper;

    @Autowired
    public SatispayController(SatispayService satispayService, ObjectMapper objectMapper) {
        this.satispayService = satispayService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<AuthenticationDto> getApi() {
        AuthenticationResponse authenticationResponse;
        try {
            authenticationResponse = satispayService.callServer(HttpMethod.GET);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthenticationDto authenticationDto = objectMapper.convertValue(authenticationResponse, AuthenticationDto.class);
        if (authenticationDto.getAuthentication_key().getRole().equals("PUBLIC"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationDto);
        return ResponseEntity.ok().body(authenticationDto);
    }

    @PostMapping
    public ResponseEntity<AuthenticationDto> postApi() {
        AuthenticationResponse authenticationResponse;
        try {
            authenticationResponse = satispayService.callServer(HttpMethod.POST);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthenticationDto authenticationDto = objectMapper.convertValue(authenticationResponse, AuthenticationDto.class);
        if (authenticationDto.getAuthentication_key().getRole().equals("PUBLIC"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationDto);
        return ResponseEntity.ok().body(authenticationDto);
    }

    @PutMapping
    public ResponseEntity<AuthenticationDto> putApi() {
        AuthenticationResponse authenticationResponse;
        try {
            authenticationResponse = satispayService.callServer(HttpMethod.PUT);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthenticationDto authenticationDto = objectMapper.convertValue(authenticationResponse, AuthenticationDto.class);
        if (authenticationDto.getAuthentication_key().getRole().equals("PUBLIC"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationDto);
        return ResponseEntity.ok().body(authenticationDto);
    }

    @DeleteMapping
    public ResponseEntity<AuthenticationDto> deleteApi() {
        AuthenticationResponse authenticationResponse;
        try {
            authenticationResponse = satispayService.callServer(HttpMethod.DELETE);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthenticationDto authenticationDto = objectMapper.convertValue(authenticationResponse, AuthenticationDto.class);
        if (authenticationDto.getAuthentication_key().getRole().equals("PUBLIC"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationDto);
        return ResponseEntity.ok().body(authenticationDto);
    }
}
