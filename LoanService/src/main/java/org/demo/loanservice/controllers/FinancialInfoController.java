package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.FinancialInfoRq;
import org.demo.loanservice.services.IFinancialInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/financial-info")
public class FinancialInfoController {
    private final IFinancialInfoService financialInfoService;

    @PostMapping("/individual-customer/save-info")
    public ResponseEntity<DataResponseWrapper<Object>> saveInfo(
            @RequestPart(name = "incomeVerificationDocuments") List<MultipartFile> incomeVerificationDocuments,
            @RequestPart(name = "financialInfoRq") FinancialInfoRq financialInfoRq,
            @RequestHeader(name = "transactionId") String transactionId) {
        return new ResponseEntity<>(financialInfoService.saveInfoIndividualCustomer(
                financialInfoRq,
                incomeVerificationDocuments,
                transactionId), HttpStatus.OK);
    }

    @PostMapping("/individual-customer/get-all-info-pending")
    public ResponseEntity<DataResponseWrapper<Object>> getAllInfoPending(
            @RequestHeader(name = "transactionId") String transactionId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "12", required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(financialInfoService.getAllInfoIsPending(
                pageNumber,
                pageSize,
                transactionId), HttpStatus.OK);
    }
}
