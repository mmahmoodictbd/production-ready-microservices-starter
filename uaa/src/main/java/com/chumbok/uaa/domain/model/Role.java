package com.chumbok.uaa.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * User Role entity
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "role", uniqueConstraints = {@UniqueConstraint(columnNames = {"org_id", "tenant_id", "role"})})
public class Role extends BaseEntity {

    /**
     * Org is the parent of it's tenants.
     * Part of unique identifier of an user.
     */
    @ManyToOne(optional = false)
    private Org org;

    /**
     * Tenant is the parent of it's users.
     * Part of unique identifier of an user.
     */
    @ManyToOne(optional = false)
    private Tenant tenant;

    /**
     * String representation of role.
     */
    @NotNull
    @Column(nullable = false)
    @Length(min = 1, max = 255)
    private String role;

}
