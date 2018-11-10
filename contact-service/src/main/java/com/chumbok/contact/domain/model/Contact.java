package com.chumbok.contact.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Contact multi-tenant entity
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Contact extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String displayName;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email1;

    @Column
    private String email2;

    @Column
    private String mobile1;

    @Column
    private String mobile2;

    @Column
    private String phone;

    @Column
    private String addressLine1;

    @Column
    private String addressLine2;

    @Column
    private String addressLine3;

    @Column
    private String addressLine4;

    @Column
    private String city;

    @Column
    private String zip;

    @Column
    private LocalDate birthday;

    @Size(max = 256)
    @Column
    private String imageUrl;

    /**
     * Provides facility to persist more attributes.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contact_additional_properties", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();
}
