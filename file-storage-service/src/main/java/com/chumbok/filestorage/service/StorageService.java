package com.chumbok.filestorage.service;

import com.chumbok.filestorage.dto.response.FilesResponse;
import com.chumbok.filestorage.dto.response.IdentityResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    FilesResponse listByPage(Pageable pageable);

    Resource loadFileAsResource(String url);

    IdentityResponse store(MultipartFile file);
}
