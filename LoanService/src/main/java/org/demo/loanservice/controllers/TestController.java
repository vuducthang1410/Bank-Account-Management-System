package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Operation(description = "this is the demo")
    @GetMapping("/")
    public String demo(){
        return "demo";
    }
}
