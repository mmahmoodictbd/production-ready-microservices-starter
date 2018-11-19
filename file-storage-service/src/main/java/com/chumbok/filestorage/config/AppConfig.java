package com.chumbok.filestorage.config;

import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.testable.common.FileSystemUtil;
import com.chumbok.testable.common.SlugUtil;
import com.chumbok.testable.common.SystemUtil;
import com.chumbok.testable.common.UuidUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Load beans necessary objects.
 */
@Configuration
public class AppConfig {

    @Bean
    public DateUtil dateUtil() {
        return new DateUtil();
    }

    @Bean
    public UuidUtil uuidUtil() {
        return new UuidUtil();
    }

    @Bean
    public SecurityUtil securityUtil() {
        return new SecurityUtil();
    }

    @Bean
    public SlugUtil slugUtil() {
        return new SlugUtil();
    }

    @Bean
    public SystemUtil systemUtil() {
        return new SystemUtil();
    }

    @Bean
    public FileSystemUtil fileSystemUtil() {
        return new FileSystemUtil();
    }
}
