package com.chumbok.uaa.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Org entity
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Org extends BaseEntity {

    /**
     * String representation of org.
     */
    @NotNull
    @Column(unique = true, nullable = false)
    @Length(min = 1, max = 255)
    private String org;

    public Org() {
    }

    public Org(String org) {
        this.org = org;
    }

}
