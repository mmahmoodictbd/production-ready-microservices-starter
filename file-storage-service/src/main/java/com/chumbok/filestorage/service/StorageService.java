package com.chumbok.filestorage.service;

import com.chumbok.filestorage.dto.request.StoreFileCreateRequest;
import com.chumbok.filestorage.dto.response.FilesResponse;
import com.chumbok.filestorage.dto.response.IdentityResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

public interface StorageService {

    FilesResponse listByPage(Pageable pageable);

    Resource loadFileAsResource(String url);

    IdentityResponse store(StoreFileCreateRequest storeFileCreateRequest);
}
