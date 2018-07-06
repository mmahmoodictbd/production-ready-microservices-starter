package com.chumbok.uaa.conf;

import com.chumbok.uaa.util.DateUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateConfiguration {

    @Bean
    public DateUtility dateUtility() {
        return new DateUtility();
    }
}
