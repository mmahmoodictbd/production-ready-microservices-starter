package com.chumbok.uaa.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User entity
 */
@Getter
@Setter
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"domain", "email"})})
public class User extends BaseEntity {

    /**
     * Domain aka tenant/org
     * Part of unique identifier of an user.
     */
    @NotBlank
    @Column(nullable = false)
    private String domain;

    /**
     * Unique identifier of an user.
     */
    @NotBlank
    @Column(nullable = false)
    private String email;

    /**
     * Hashed password.
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * First name of an user.
     * Stored for the purpose of more personalized interactions.
     */
    @Column
    private String firstName;

    /**
     * Last name of an user.
     * Stored for the purpose of formal interactions.
     */
    @Column
    private String lastName;

    /**
     * Display name of an user.
     * Stored for the presentation purpose.
     */
    @NotBlank
    @Column(nullable = false)
    private String displayName;

    /**
     * User's image URL
     */
    @Size(max = 256)
    @Column
    private String imageUrl;

    /**
     * Time zone of the user
     * A time-zone ID, such as Europe/Paris.
     */
    @Column
    private String timezoneId;

    /**
     * Preferred language of the user.
     */
    @Column
    private String preferredLanguage;

    /**
     * Roles
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    /**
     * Provides facility to persist more attributes.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_additional_properties", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();

    /**
     * Enable/disable user
     */
    @Column
    private boolean enabled;

}
