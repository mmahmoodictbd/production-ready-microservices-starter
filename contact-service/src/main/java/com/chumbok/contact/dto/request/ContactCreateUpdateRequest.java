package com.chumbok.contact.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
public class ContactCreateUpdateRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String displayName;

    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @Size(min = 1, max = 255)
    private String email1;

    @Size(min = 1, max = 255)
    private String email2;

    @Size(min = 1, max = 255)
    private String mobile1;

    @Size(min = 1, max = 255)
    private String mobile2;

    @Size(min = 1, max = 255)
    private String phone;

    @Size(min = 1, max = 255)
    private String addressLine1;

    @Size(min = 1, max = 255)
    private String addressLine2;

    @Size(min = 1, max = 255)
    private String addressLine3;

    @Size(min = 1, max = 255)
    private String addressLine4;

    @Size(min = 1, max = 255)
    private String city;

    @Size(min = 1, max = 255)
    private String zip;

    @Size(min = 1, max = 255)
    private String birthday;

    @Size(min = 1, max = 255)
    private String imageUrl;

    private Map<String, String> additionalProperties;
}
