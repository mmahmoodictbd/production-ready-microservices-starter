package com.chumbok.uaa.conf;


import com.chumbok.security.util.EncryptionKeyUtil;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.CookieUtil;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.testable.common.UrlUtil;
import com.chumbok.testable.common.UuidUtil;
import com.chumbok.uaa.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public EncryptionKeyUtil encryptionKeyUtil() {
        return new EncryptionKeyUtil();
    }

    @Bean
    public DateUtil dateUtil() {
        return new DateUtil();
    }

    @Bean
    public UrlUtil urlUtil() {
        return new UrlUtil();
    }

    @Bean
    public CookieUtil cookieUtil() {
        return new CookieUtil();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public SecurityUtil securityUtil() {
        return new SecurityUtil();
    }

    @Bean
    public UuidUtil uuidUtil() {
        return new UuidUtil();
    }
}
