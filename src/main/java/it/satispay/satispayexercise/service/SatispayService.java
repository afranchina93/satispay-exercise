package it.satispay.satispayexercise.service;

import it.satispay.satispayexercise.service.satispay.response.AuthenticationResponse;
import org.springframework.http.HttpMethod;

public interface SatispayService {
    AuthenticationResponse callServer(HttpMethod httpMethod);
}
