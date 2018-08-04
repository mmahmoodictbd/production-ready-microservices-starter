package com.chumbok.uaa.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * User Role entity
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Role extends BaseEntity {

    /**
     * String representation of role.
     */
    @Column(unique = true)
    @NotNull
    @Length(min = 1, max = 255)
    private String role;

}
