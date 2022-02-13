package it.satispay.satispayexercise.controller;

import it.satispay.satispayexercise.service.SatispayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        ResponseEntity<String> response = satispayService.callServer();
        return ResponseEntity.ok().body(response);
    }
}
