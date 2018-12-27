package com.chumbok.blog.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@MappedSuperclass
public class Post extends BaseEntity {

    @Column
    @Length(min = 1, max = 255)
    private String title;

    @Column
    @Lob
    private String content;

    @Column
    @Lob
    private String summary;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Tag> tags = new LinkedHashSet<>();

    @Column
    @Length(max = 255)
    private String permalink;

    @Column
    @Length(max = 255)
    private String featureImageLink;

}
