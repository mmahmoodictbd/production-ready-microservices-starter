package com.chumbok.uaa.controller;

import com.chumbok.uaa.IntegrationTestBase;
import io.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class PingControllerIT extends IntegrationTestBase {

    @Test
    public void shouldReturnHttpStatus204WhenPing() {
        Response response = given().get("/ping").andReturn();
        response.then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());
    }


}