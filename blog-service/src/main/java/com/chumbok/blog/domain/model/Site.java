package com.chumbok.blog.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class Site extends BaseEntity {

    @Column
    @Length(min = 1, max = 255)
    private String name;

    @Column
    @Lob
    private String description;

    @Column
    @Length(min = 1, max = 255)
    private String siteUrl;

    @Column
    @Length(min = 1, max = 255)
    private String homeUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "site_additional_properties", joinColumns = @JoinColumn(name = "site_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();

}
