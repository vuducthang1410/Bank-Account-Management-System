package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.ApproveFinancialInfoRq;
import org.demo.loanservice.dto.request.FinancialInfoRq;
import org.demo.loanservice.services.IFinancialInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/individual-customer/get-all-info-by-status")
    public ResponseEntity<DataResponseWrapper<Object>> getAllInfoByStatus(
            @RequestHeader(name = "transactionId") String transactionId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "12", required = false) Integer pageSize,
            @RequestParam(name = "status",defaultValue = "PENDING",required = false) String status
    ) {
        return new ResponseEntity<>(financialInfoService.getAllInfoIsByStatus(
                pageNumber,
                pageSize,
                status,
                transactionId), HttpStatus.OK);
    }
    @GetMapping("/individual-customer/get-detail-info/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> getDetailInfo(
            @PathVariable(name = "id")String id,
            @RequestHeader(name = "transactionId")String transactionId
    ){
        return new ResponseEntity<>(financialInfoService.getDetailInfoById(id,transactionId),HttpStatus.OK);
    }
    @PatchMapping("/individual-customer/financial-info/approve")
    public ResponseEntity<DataResponseWrapper<Object>> approveFinancialInfo(
            @RequestBody ApproveFinancialInfoRq approveFinancialInfoRq,
            @RequestHeader(name = "transactionId")String transactionId
    ){
        return new ResponseEntity<>(financialInfoService.approveFinancialInfo(approveFinancialInfoRq,transactionId),HttpStatus.OK);
    }
    @GetMapping("/individual-customer/financial-info/verify")
    public ResponseEntity<DataResponseWrapper<Object>> verifyFinancialInfo(
        @RequestHeader("transactionId")String transactionId
    ){
        return new ResponseEntity<>(financialInfoService.verifyFinancialInfo(transactionId),HttpStatus.OK);
    }
}
