package com.chumbok.uaa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orgs")
public class OrgController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, String> connect() {
        return Collections.singletonMap("x", "y");
    }
}
