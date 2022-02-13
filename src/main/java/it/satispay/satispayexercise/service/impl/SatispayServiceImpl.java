package it.satispay.satispayexercise.service.impl;

import it.satispay.satispayexercise.service.SatispayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SatispayServiceImpl implements SatispayService {

    private final static String URI = "https://staging.authservices.satispay.com/wally-services/protocol/tests/signature";

    @Override
    public String callServer() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(URI, String.class);
        log.info(response);
        return response;
    }
}
