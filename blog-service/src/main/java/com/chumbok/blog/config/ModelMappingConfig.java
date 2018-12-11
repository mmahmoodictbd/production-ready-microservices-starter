package com.chumbok.blog.config;

import com.chumbok.blog.domain.model.Site;
import com.chumbok.blog.dto.request.SiteCreateUpdateRequest;
import com.chumbok.blog.dto.response.SiteResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMappingConfig {

    @Bean
    public ModelMapper createModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        addMappingSiteCreateUpdateRequestToSite(modelMapper);
        addMappingSiteToSiteResponse(modelMapper);

        return modelMapper;
    }

    private void addMappingSiteCreateUpdateRequestToSite(ModelMapper mapper) {

        PropertyMap propertyMap = new PropertyMap<SiteCreateUpdateRequest, Site>() {

            @Override
            protected void configure() {

                skip(destination.getId());
                skip(destination.getCreatedBy());
                skip(destination.getUpdatedBy());
                skip(destination.getCreatedAt());
                skip(destination.getUpdatedAt());
                skip(destination.getOrg());
                skip(destination.getTenant());
            }
        };

        mapper.createTypeMap(SiteCreateUpdateRequest.class, Site.class).addMappings(propertyMap);
        mapper.validate();
    }

    private void addMappingSiteToSiteResponse(ModelMapper mapper) {
        mapper.createTypeMap(Site.class, SiteResponse.class);
        mapper.validate();
    }

}
