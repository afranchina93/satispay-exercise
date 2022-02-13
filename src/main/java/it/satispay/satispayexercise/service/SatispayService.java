package it.satispay.satispayexercise.service;

import it.satispay.satispayexercise.exception.AuthenticationException;
import it.satispay.satispayexercise.service.response.AuthenticationResponse;
import org.springframework.http.HttpMethod;

public interface SatispayService {
    AuthenticationResponse callServer(HttpMethod httpMethod) throws AuthenticationException;
}
