package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.demo.loanservice.validatedCustom.interfaceValidate.LoanStatusValidation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(Util.API_RESOURCE + "/loan-detail-info")
public class LoanDetailInfoController {
    private final ILoanDetailInfoService loanDetailInfoService;

    @PostMapping("/individual-customer/register-loan")
    public ResponseEntity<DataResponseWrapper<Object>> registerIndividualCustomerLoan(
            @RequestBody IndividualCustomerInfoRq individualCustomerInfoRq,
            @RequestHeader(name = "transactionId") String transactionId
    ) {
        return ResponseEntity.ok(loanDetailInfoService.registerIndividualCustomerLoan(
                individualCustomerInfoRq,
                transactionId));
    }

    @PatchMapping("/individual-customer/approve-disbursement")
    public ResponseEntity<DataResponseWrapper<Object>> approveIndividualCustomerDisbursement(
            @RequestBody LoanInfoApprovalRq loanInfoApprovalRq,
            @RequestHeader(name = "transactionId") String transactionId
    ) {
        return ResponseEntity.ok(
                loanDetailInfoService.approveIndividualCustomerDisbursement(loanInfoApprovalRq, transactionId)
        );
    }

    @GetMapping("/get-all-by-loan-status")
    public ResponseEntity<DataResponseWrapper<Object>> getAllByLoanStatus(
            @RequestParam(name = "loanStatus") @LoanStatusValidation String loanStatus,
            @RequestHeader(name = "transactionId") String transactionId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false)
            @Schema(description = "Page number for pagination", example = "0")
            Integer pageNumber,

            @RequestParam(name = "pageSize", defaultValue = "12", required = false)
            @Schema(description = "Page size for pagination", example = "12")
            Integer pageSize
    ) {
        return ResponseEntity.ok(loanDetailInfoService.getAllByLoanStatus(loanStatus, pageNumber, pageSize, transactionId));
    }

    @GetMapping("/get-all-loan-info-by-customer-id")
    public ResponseEntity<DataResponseWrapper<Object>> getAllLoanInfoByCustomerId(
            @RequestHeader(name = "transactionId") String transactionId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false)
            @Schema(description = "Page number for pagination", example = "0")
            Integer pageNumber,

            @RequestParam(name = "pageSize", defaultValue = "12", required = false)
            @Schema(description = "Page size for pagination", example = "12")
            Integer pageSize
    ) {
        return ResponseEntity.ok(loanDetailInfoService.getAllByCustomerId(pageNumber, pageSize, transactionId));
    }

}
