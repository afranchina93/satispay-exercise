package it.satispay.satispayexercise.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface SatispayService {
    ResponseEntity<String> callServer(HttpMethod httpMethod);
}
