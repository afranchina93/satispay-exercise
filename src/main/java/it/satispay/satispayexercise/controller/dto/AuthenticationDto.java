package it.satispay.satispayexercise.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class AuthenticationDto {
    private AuthenticationKeyDto authentication_key;
    private SignatureDto signature;
    private String signed_string;

    @Data
    public static class AuthenticationKeyDto {
        private String access_key;
        private String customer_uid;
        private int sequence;
        private String key_type;
        private String auth_type;
        private String role;
        private boolean enable;
        private Date insert_date;
        private Date update_date;
        private int version;
    }

    @Data
    public static class SignatureDto {
        private String key_id;
        private String algorithm;
        private ArrayList<String> headers;
        private String signature;
        private boolean resign_required;
        private boolean valid;
        private int iteration_count;
    }
}
