package com.chumbok.contact.domain.model;


import com.chumbok.multitenancy.entity.BaseTenantEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class, provides common attributes
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTenantEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id of persisted entity.
     */
    @Id
    private String id;

    /**
     * Username (aka email) of creator.
     * Set in com.chumbok.apptmngr.conf.JpaAuditConfig class.
     * Auto updated by Hibernate.
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * Date time when entity got persisted first time by Hibernate.
     * Timezone is set to UTC in com.chumbok.apptmngr.Application class.
     * Auto updated by Hibernate
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Username (aka email) of updater.
     * Set in com.chumbok.apptmngr.conf.JpaAuditConfig class.
     * Auto updated by Hibernate.
     */
    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;


    /**
     * Date time when entity got updated by Hibernate.
     * Timezone is set to UTC in com.chumbok.apptmngr.Application class.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


}