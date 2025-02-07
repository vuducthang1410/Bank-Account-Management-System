package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-detail-info")
public class LoanDetailInfoController {
    private final ILoanDetailInfoService loanDetailInfoService;

    @PostMapping("/individual-customer/register-loan")
    public ResponseEntity<DataResponseWrapper<Object>> registerIndividualCustomerLoan(
            @RequestBody IndividualCustomerInfoRq individualCustomerInfoRq,
            @RequestHeader(name = "transactionId") String transactionId
    ) {
        return new ResponseEntity<>(loanDetailInfoService.registerIndividualCustomerLoan(
                individualCustomerInfoRq,
                transactionId), HttpStatus.OK);
    }

}
