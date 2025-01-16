package org.demo.accountservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {
    @GetMapping("/")
    public String home() {
        return "Hello World";
    }
    @PostMapping("/demo")
    public String demo(@RequestBody Map<String,String> name) {
        return "Hello World"+name;
    }
}
