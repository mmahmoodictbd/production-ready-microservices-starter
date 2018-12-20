package com.chumbok.blog.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Tag extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Site site;

    @Column(unique = true)
    @Length(min = 1, max = 255)
    private String name;

    @Column(unique = true)
    @Length(min = 1, max = 255)
    private String slug;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tag_additional_properties", joinColumns = @JoinColumn(name = "tag_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();

}
