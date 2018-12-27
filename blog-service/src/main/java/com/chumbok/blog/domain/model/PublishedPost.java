package com.chumbok.blog.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
public class PublishedPost extends Post {

    @Column
    private Date publishDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "published_post_additional_properties", joinColumns = @JoinColumn(name = "published_post_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();

}
