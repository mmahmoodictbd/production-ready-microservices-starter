package com.chumbok.filestorage.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class FilesResponse {

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<FileResponse> items;
}
