package com.chumbok.filestorage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {

    private String id;
    private String path;
    private String originalName;
    private LocalDateTime createdAt;
    private Map<String, String> additionalProperties;

}
