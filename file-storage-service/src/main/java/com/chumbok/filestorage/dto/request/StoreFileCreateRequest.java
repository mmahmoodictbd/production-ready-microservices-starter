package com.chumbok.filestorage.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Builder
public class StoreFileCreateRequest {

    @NotNull
    private MultipartFile file;

    private boolean publicFile;

    private Map<String, String> additionalProperties;

}
