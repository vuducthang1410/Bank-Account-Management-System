package org.demo.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.dto.request.DeftRepaymentRq;
import org.demo.loanservice.services.IPaymentScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Util.API_RESOURCE+"/payment-schedule")
public class PaymentScheduleController {
    private final IPaymentScheduleService paymentScheduleService;

    @PatchMapping("/repaymentDeftPeriodically")
    public ResponseEntity<DataResponseWrapper<Object>> repaymentDeftPeriodically(
            @RequestHeader(name = "transactionId") String transactionId,
            @RequestBody DeftRepaymentRq deftRepaymentRq
    ) {
        return new ResponseEntity<>(paymentScheduleService.automaticallyRepaymentDeftPeriodically(deftRepaymentRq, transactionId), HttpStatus.OK);
    }
    @GetMapping("/get-list-payment-schedule/{loanInfoId}")
    public ResponseEntity<DataResponseWrapper<Object>> getListPaymentScheduleByLoanDetailInfo(
            @PathVariable(name = "loanInfoId")String loanInfoId,
            @RequestParam(name = "pageSize",required = false,defaultValue = "12")Integer pageSize,
            @RequestParam(name = "pageNumber",required = false,defaultValue = "0")Integer pageNumber,
            @RequestHeader(name = "transactionId")String transactionId
    ){
        return new ResponseEntity<>(paymentScheduleService.getListPaymentScheduleByLoanDetailInfo(loanInfoId,pageSize,pageNumber,transactionId),HttpStatus.OK);
    }
}
