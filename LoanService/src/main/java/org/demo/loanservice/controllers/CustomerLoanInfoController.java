package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.services.ICustomerLoanInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer-loan-info")
public class CustomerLoanInfoController {
    private final ICustomerLoanInfoService customerLoanInfoService;


}
