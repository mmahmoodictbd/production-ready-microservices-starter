package com.chumbok.filestorage.service;

import com.chumbok.exception.common.IORuntimeException;
import com.chumbok.filestorage.dto.request.StoreFileCreateRequest;
import com.chumbok.filestorage.dto.response.FilesResponse;
import com.chumbok.filestorage.dto.response.IdentityResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GoogleDriveStorageService implements StorageService {

    private static final String GOOGLE_DRIVE_APIS_SCOPES = "https://www.googleapis.com/auth/drive.file";

    private final String googleDriveServiceAccountCredentialsFilePath;
    private final String googleDriveServiceAccountAdminEmail;
    private final RestTemplate restTemplate;

    public GoogleDriveStorageService(String googleDriveServiceAccountCredentialsFilePath,
                                     String googleDriveServiceAccountAdminEmail,
                                     RestTemplate restTemplate) {

        Objects.requireNonNull(googleDriveServiceAccountCredentialsFilePath);
        Objects.requireNonNull(googleDriveServiceAccountAdminEmail);

        this.googleDriveServiceAccountCredentialsFilePath = googleDriveServiceAccountCredentialsFilePath;
        this.googleDriveServiceAccountAdminEmail = googleDriveServiceAccountAdminEmail;
        this.restTemplate = restTemplate;

        if (!isInitialized()) {
            initialize();
        }
    }

    @Override
    public FilesResponse listByPage(Pageable pageable) {
        return null;
    }

    @Override
    public Resource loadFileAsResource(String url) {
        return null;
    }

    @Override
    public IdentityResponse store(StoreFileCreateRequest storeFileCreateRequest) {
        return null;
    }

    private void initialize() {

        String createServiceRootDirectoryUrl = "https://www.googleapis.com/drive/v3/files";

        Map<String, String> body = new HashMap<>();
        body.put("name", "file-storage-service");
        body.put("mimeType", "application/vnd.google-apps.folder");

        HttpHeaders headers = buildAuthorizationHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(createServiceRootDirectoryUrl, HttpMethod.POST,
                entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.debug(String.format("initialize() Response: %s", response.getBody()));
            throw new IllegalStateException("Could not create file-storage-service directory from initialize().");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException ex) {
            throw new IORuntimeException("Could not read json.", ex);
        }

        String rootDirId = root.path("id").asText();

        String createServiceRootDirectoryPermissionUrl
                = String.format("https://www.googleapis.com/drive/v3/files/%s/permissions", rootDirId);

        body = new HashMap<>();
        body.put("role", "reader");
        body.put("type", "user");
        body.put("emailAddress", googleDriveServiceAccountAdminEmail);
        entity = new HttpEntity(body, headers);

        response = restTemplate.exchange(createServiceRootDirectoryPermissionUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.debug(String.format("initialize() Response: %s", response.getBody()));
            throw new IllegalStateException("Could not create permission for file-storage-service from initialize().");
        }
    }

    private boolean isInitialized() {

        String fileSearchUrl = String.format("https://www.googleapis.com/drive/v3/files?q=%s",
                "name='file-storage-service'&mimeType='application/vnd.google-apps.folder'");

        HttpEntity entity = new HttpEntity(buildAuthorizationHeader());
        ResponseEntity<String> response = restTemplate.exchange(fileSearchUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException(String.format("Error from Google Drive service, Response: %s",
                    response.toString()));
        }

        log.debug(String.format("isInitialized() Response: %s", response.getBody()));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException ex) {
            throw new IORuntimeException("Could not read json.", ex);
        }
        JsonNode filesNode = root.path("files");
        if (filesNode != null && filesNode.isArray() && filesNode.size() == 1) {
            JsonNode firstFileNode = filesNode.iterator().next();
            if (firstFileNode != null && "file-storage-service".equals(firstFileNode.get("name").asText())) {
                return true;
            }
        }

        return false;
    }

    private HttpHeaders buildAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        return headers;
    }

    private String getAccessToken() {

        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream(googleDriveServiceAccountCredentialsFilePath));
            credentials = credentials.createScoped(GOOGLE_DRIVE_APIS_SCOPES);
            credentials.refreshIfExpired();
        } catch (IOException ex) {
            throw new IORuntimeException("Could not create GoogleCredentials.", ex);
        }

        AccessToken token = credentials.getAccessToken();
        return token.getTokenValue();
    }
}
