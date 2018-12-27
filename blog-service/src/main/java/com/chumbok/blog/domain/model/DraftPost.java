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
import javax.persistence.OneToOne;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
public class DraftPost extends Post {

    @OneToOne(fetch = FetchType.LAZY)
    private PublishedPost publishedPost;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "draft_post_additional_properties", joinColumns = @JoinColumn(name = "draft_post_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();
}
