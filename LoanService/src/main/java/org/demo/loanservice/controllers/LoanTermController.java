package org.demo.loanservice.controllers;//package org.demo.loanservice.controllers;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.demo.loanservice.dto.request.InterestRateRq;
//import org.demo.loanservice.dto.response.DataResponseWrapper;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/loan-term")
//@Schema
//public class LoanTermController {
//    @PostMapping("/save")
//    public ResponseEntity<DataResponseWrapper<Object>> save(
//            @RequestBody @Valid InterestRateRq interestRateRq,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.save(interestRateRq, transactionId), HttpStatus.CREATED);
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> getById(
//            @Parameter(description = "ID of the interest rate to be retrieved") @PathVariable(name = "id") String id,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.getById(id, transactionId), HttpStatus.OK);
//    }
//    @GetMapping("/get-all")
//    public ResponseEntity<DataResponseWrapper<Object>> getAll(
//            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
//            @RequestParam(name = "pageSize", defaultValue = "12", required = false) Integer pageSize,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.getAll(pageNumber, pageSize, transactionId), HttpStatus.OK);
//    }
//    @PatchMapping("/active/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> active(
//            @PathVariable(name = "id") String id,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.active(id, transactionId), HttpStatus.OK);
//    }
//    @PatchMapping("/update/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> update(
//            @RequestBody InterestRateRq interestRateRq,
//            @PathVariable(name = "id") String id,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.update(id, interestRateRq, transactionId), HttpStatus.OK);
//    }
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> delete(
//            @PathVariable(name = "id") String id,
//            @RequestHeader(name = "transactionId") String transactionId
//    ) {
//        return new ResponseEntity<>(interestRateService.delete(id, transactionId), HttpStatus.OK);
//    }
//}
