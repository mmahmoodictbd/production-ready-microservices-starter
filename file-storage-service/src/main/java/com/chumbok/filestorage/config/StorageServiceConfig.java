package com.chumbok.filestorage.config;

import com.chumbok.filestorage.domain.repository.FileRepository;
import com.chumbok.filestorage.service.FileSystemStorageService;
import com.chumbok.filestorage.service.GoogleDriveStorageService;
import com.chumbok.filestorage.service.StorageService;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.testable.common.FileSystemUtil;
import com.chumbok.testable.common.SlugUtil;
import com.chumbok.testable.common.SystemUtil;
import com.chumbok.testable.common.UuidUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class StorageServiceConfig {

    private static final String GOOGLE_DRIVE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH
            = "com.chumbok.file-storage-service.googledrive.service-account-credentials-file-path";
    private static final String GOOGLE_DRIVE_SERVICE_ACCOUNT_EMAIL
            = "com.chumbok.file-storage-service.googledrive.service-account-admin-email";

    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final FileRepository fileRepository;
    private final DateUtil dateUtil;
    private final UuidUtil uuidUtil;
    private final SystemUtil systemUtil;
    private final FileSystemUtil fileSystemUtil;
    private final SlugUtil slugUtil;
    private final SecurityUtil securityUtil;
    private final RestTemplate restTemplate;

    @Bean
    public StorageService storageService() {

        if ("GoogleDrive".equals(environment.getProperty("com.chumbok.file-storage-service.mode"))) {
            return new GoogleDriveStorageService(
                    environment.getProperty(GOOGLE_DRIVE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH),
                    environment.getProperty(GOOGLE_DRIVE_SERVICE_ACCOUNT_EMAIL),
                    restTemplate
            );
        }

        return new FileSystemStorageService(resourceLoader, fileRepository, dateUtil, uuidUtil,
                systemUtil, fileSystemUtil, slugUtil, securityUtil);
    }
}
