package com.chumbok.filestorage.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class File extends BaseEntity {

    @NotBlank
    @Length(max = 500)
    @Column
    private String path;

    @NotBlank
    @Column
    private String originalName;

    /**
     * If it's public file or not.
     */
    private boolean secured;

    /**
     * Provides facility to persist more attributes.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_additional_properties", joinColumns = @JoinColumn(name = "file_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> additionalProperties = new HashMap<>();

}
