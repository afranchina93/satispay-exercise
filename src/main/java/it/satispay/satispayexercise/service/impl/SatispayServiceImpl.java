package it.satispay.satispayexercise.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.satispay.satispayexercise.exception.AuthenticationException;
import it.satispay.satispayexercise.service.SatispayService;
import it.satispay.satispayexercise.service.response.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@Service
public class SatispayServiceImpl implements SatispayService {

    private final String host;

    private final String path;

    private final String keyId;

    @Autowired
    private ObjectMapper objectMapper;

    public SatispayServiceImpl(@Value("${satispay.host}") String host, @Value("${satispay.path}") String path, @Value("${satispay.key-id}") String keyId) {
        this.host = host;
        this.path = path;
        this.keyId = keyId;
    }

    @Override
    public AuthenticationResponse callServer(HttpMethod httpMethod) throws AuthenticationException {
        RestTemplate restTemplate = new RestTemplate();

        String rsaPrivateKey = "";
        try {
            rsaPrivateKey = readPrivateKey(new File("src/main/resources/client-rsa-private-key.pem"));
        } catch (Exception e) {
            log.error("Error retrieving privateKey: " + e.getMessage());
        }

        String digestValue = null;
        try {
            digestValue = "sha256=" + signSHA256RSA(keyId, rsaPrivateKey);
        } catch (Exception e) {
            log.error("Error calculating privateKey " + e.getMessage());
        }

        StringBuilder contentType = new StringBuilder(" ");
        if (!httpMethod.equals(HttpMethod.GET)) {
            contentType.append("content-type ");
        }

        LocalDateTime dateTime = LocalDateTime.now();

        StringBuilder input = retrieveInput(httpMethod, digestValue, dateTime);

        String signature = "";
        try {
            signature = signSHA256RSA(input.toString(), rsaPrivateKey);
        } catch (Exception e) {
            log.error("Error calculating signature: " + e.getMessage());
        }

        HttpHeaders headers = retrieveHeaders(httpMethod, digestValue, dateTime);
        headers.set("Authorization", "Signature keyId=\"" + keyId + "\", algorithm=\"rsa-sha256\", headers=\"(request-target) host date" + contentType + "digest\", signature=\"" + signature + "\"");
        HttpEntity<?> requestEntity;
        switch (httpMethod) {
            case POST:
            case PUT:
                requestEntity = new HttpEntity<>("{\"hello\": \"world\"}", headers);
                break;
            default:
                requestEntity = new HttpEntity<>(null, headers);
                break;
        }

        ResponseEntity<String> exchange = restTemplate.exchange(UriComponentsBuilder.fromHttpUrl("https://" + host + path)
                .encode()
                .toUriString(), httpMethod, requestEntity, String.class);

        if (!exchange.getStatusCode().is2xxSuccessful()) throw new AuthenticationException();

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        try {
            authenticationResponse = objectMapper.readValue(exchange.getBody(), AuthenticationResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Error mapping response: " + e.getMessage());
        }
        return authenticationResponse;
    }

    private HttpHeaders retrieveHeaders(HttpMethod httpMethod, String digestValue, LocalDateTime dateTime) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", host);
        headers.set("Date", dateTime.toString());
        if (!httpMethod.equals(HttpMethod.GET)) {
            headers.set("Content-Type", "application/json");
        }
        headers.set("Digest", digestValue);
        return headers;
    }

    private StringBuilder retrieveInput(HttpMethod httpMethod, String digestValue, LocalDateTime dateTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("(request-target): ").append(httpMethod.toString().toLowerCase(Locale.ROOT)).append(" ").append(path).append(System.lineSeparator())
                .append("host: ").append(host).append(System.lineSeparator())
                .append("date: ").append(dateTime).append(System.lineSeparator());
        if (!httpMethod.equals(HttpMethod.GET)) {
            stringBuilder.append("content-type: application/json\n");
        }
        stringBuilder.append("digest: ").append(digestValue);
        return stringBuilder;
    }

    private String readPrivateKey(File file) throws Exception {
        String key = Files.readString(file.toPath(), Charset.defaultCharset());
        return key.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");
    }

    private String signSHA256RSA(String input, String strPk) throws Exception {
        byte[] b1 = Base64.decodeBase64(strPk);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] s = privateSignature.sign();
        return Base64.encodeBase64String(s);
    }
}
