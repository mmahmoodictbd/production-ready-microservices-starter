package com.chumbok.filestorage.controller;

import com.chumbok.filestorage.dto.request.StoreFileCreateRequest;
import com.chumbok.filestorage.dto.response.FilesResponse;
import com.chumbok.filestorage.dto.response.IdentityResponse;
import com.chumbok.filestorage.service.StorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Controller
public class FileStorageController {

    private final StorageService storageService;


    @GetMapping("/files")
    public ResponseEntity<FilesResponse> notificationPage(Pageable pageable) {
        return new ResponseEntity<>(storageService.listByPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/public/files/**")
    public ResponseEntity<Resource> downloadPublicFile(HttpServletRequest request) {
        return getResourceResponseEntity(request);
    }

    @GetMapping("/files/**")
    public ResponseEntity<Resource> downloadSecureFile(HttpServletRequest request) {
        return getResourceResponseEntity(request);
    }

    @PostMapping("/files")
    public ResponseEntity<IdentityResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "public", required = false) boolean publicFile) {

        StoreFileCreateRequest request = StoreFileCreateRequest.builder().file(file).publicFile(publicFile).build();
        return new ResponseEntity<>(storageService.store(request), HttpStatus.CREATED);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(HttpServletRequest request) {

        String url = request.getRequestURL().toString().split("/files/")[1];
        if (url == null || url.trim().length() == 0) {
            // TODO: use custom exception
            throw new IllegalArgumentException("Bad URL.");
        }

        Resource resource = storageService.loadFileAsResource(url);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
