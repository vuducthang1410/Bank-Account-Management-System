package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.FormDeftRepaymentRq;
import org.demo.loanservice.services.IFormDeftRepaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form-deft-repayment")
@Schema(description = "FormDeftRepaymentController")
public class FormDeftRepaymentController {
    private final IFormDeftRepaymentService formDeftRepaymentService;
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> getById(
            @PathVariable("id") String id,
            @RequestHeader("transactionId") String transactionId
    ) {
        return ResponseEntity.ok(formDeftRepaymentService.getById(id, transactionId));
    }
    @GetMapping("/get-all")
    public ResponseEntity<DataResponseWrapper<Object>> getAll(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "12", required = false) Integer pageSize,
            @RequestHeader(name = "transactionId") String transactionId
    ){
        return ResponseEntity.ok(formDeftRepaymentService.getAll(pageNumber,pageSize,transactionId));
    }
    @PostMapping("/save")
    public ResponseEntity<DataResponseWrapper<Object>> save(
            @RequestBody @Valid FormDeftRepaymentRq formDeftRepaymentRq,
            @RequestHeader(name = "transactionId")String transactionId
            ){
        return ResponseEntity.ok(formDeftRepaymentService.save(formDeftRepaymentRq,transactionId));
    }
    @PatchMapping("/active/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> active(
            @PathVariable(name ="id")String id,
            @RequestHeader(name ="transactionId") String transactionId
    ){
        return ResponseEntity.ok(formDeftRepaymentService.active(id,transactionId));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> delete(
            @PathVariable(name = "id")String id,
            @RequestHeader(name = "transactionId")String transactionId
    ){
        return ResponseEntity.ok(formDeftRepaymentService.delete(id,transactionId));
    }
}
