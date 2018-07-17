package com.chumbok.uaa.conf;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ExceptionHandlingAdvice implements ProblemHandling {

}
