package it.satispay.satispayexercise.controller;

import it.satispay.satispayexercise.service.SatispayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/satispay")
public class SatispayController {

    private final SatispayService satispayService;

    @Autowired
    public SatispayController(SatispayService satispayService) {
        this.satispayService = satispayService;
    }

    @GetMapping
    public ResponseEntity<?> getApi(){
        ResponseEntity<String> response = satispayService.callServer(HttpMethod.GET);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> postApi(){
        ResponseEntity<String> response = satispayService.callServer(HttpMethod.POST);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> putApi(){
        ResponseEntity<String> response = satispayService.callServer(HttpMethod.PUT);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteApi(){
        ResponseEntity<String> response = satispayService.callServer(HttpMethod.DELETE);
        return ResponseEntity.ok().body(response);
    }
}
