package com.chumbok.contact.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ContactResponse {

    private String id;

    private String displayName;

    private String firstName;

    private String lastName;

    private String email1;

    private String email2;

    private String mobile1;

    private String mobile2;

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String addressLine4;

    private String city;

    private String zip;

    private String birthday;

    private String imageUrl;

    private Map<String, String> additionalProperties;
}
