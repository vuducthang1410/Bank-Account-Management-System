package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Util.API_RESOURCE+"/loan-detail-info")
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
    @PatchMapping("/individual-customer/approve-disbursement")
    public ResponseEntity<DataResponseWrapper<Object>> approveIndividualCustomerDisbursement(
            @RequestBody LoanInfoApprovalRq loanInfoApprovalRq,
            @RequestHeader(name = "transactionId")String transactionId
    ){
        return new ResponseEntity<>(
                loanDetailInfoService.approveIndividualCustomerDisbursement(loanInfoApprovalRq,transactionId)
                ,HttpStatus.OK);
    }
}
