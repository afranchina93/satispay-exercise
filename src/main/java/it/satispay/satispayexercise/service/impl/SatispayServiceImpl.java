package it.satispay.satispayexercise.service.impl;

import it.satispay.satispayexercise.service.SatispayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
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

@Slf4j
@Service
public class SatispayServiceImpl implements SatispayService {

    private final static String HOST = "staging.authservices.satispay.com";
    private final static String PATH = "/wally-services/protocol/tests/signature";
    private final static String KEY_ID = "signature-test-66289";

    @Override
    public ResponseEntity<String> callServer(){
        RestTemplate restTemplate = new RestTemplate();

        String rsaPrivateKey = "";
        try {
            rsaPrivateKey = readPrivateKey(new File("src/main/resources/client-rsa-private-key.pem"));
        } catch (Exception e) {
            log.error("Error when retrieve privateKey");
        }

        String digestValue = null;
        try {
            digestValue = "sha256="+readPublicKey(new File("src/main/resources/client-rsa-public-key.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalDateTime dateTime = LocalDateTime.now();

        String originalString = "(request-target): get /wally-services/protocol/tests/signature\n"+
                "host: staging.authservices.satispay.com\n" +
                "date: "+dateTime+"\n" +
                "digest: "+digestValue;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", HOST);
        headers.set("Date", dateTime.toString());
        headers.set("Digest", digestValue);

        String signature = "";
        try {
            signature = signSHA256RSA(originalString, rsaPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        headers.set("Authorization", "Signature keyId=\""+KEY_ID+"\", algorithm=\"rsa-sha256\", headers=\"(request-target) host date digest\", signature=\""+signature+"\"");

        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(UriComponentsBuilder.fromHttpUrl("https://"+HOST+PATH)
                .encode()
                .toUriString(), HttpMethod.GET, requestEntity, String.class);
    }

    public String readPublicKey(File file) throws Exception {
        String key = Files.readString(file.toPath(), Charset.defaultCharset());
        return key.replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("\n", "");
    }

    public String readPrivateKey(File file) throws Exception {
        String key = Files.readString(file.toPath(), Charset.defaultCharset());
        return key.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");
    }

    // Create base64 encoded signature using SHA256/RSA.
    private static String signSHA256RSA(String input, String strPk) throws Exception {
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
