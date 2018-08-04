package com.chumbok.uaa;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestBase.class);

    @LocalServerPort
    private int servicePort;

    @BeforeClass
    public static void init() throws IOException {
    }

    @Before
    public void configureServicePort() {
        logger.info("Configure RestAssured port to point to service port: {}", servicePort);
        RestAssured.port = servicePort;
    }

    public int getServicePort() {
        return servicePort;
    }

}
