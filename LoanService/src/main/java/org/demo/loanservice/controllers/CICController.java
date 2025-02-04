package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.dto.CICRequest;
import org.demo.loanservice.wiremockService.CICService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cic")
@RequiredArgsConstructor
public class CICController {

    private final CICService cicService;

    @PostMapping("/credit-score")
    public Object getCreditScore(@RequestBody CICRequest request) {
        return cicService.getCreditScore(request.cccd(), request.fullName(), request.dob(), request.phoneNumber());
    }
}
