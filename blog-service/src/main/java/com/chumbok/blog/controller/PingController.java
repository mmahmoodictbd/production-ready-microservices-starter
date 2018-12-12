package com.chumbok.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ping handlers. Frontend calls /ping to insure it's connected with backend aka internet.
 */
@RestController
public class PingController {

    /**
     * Just to insure it's connected.
     * Returns 204 No Content.
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void connect() {
        // Method is intentionally empty.
    }
}
