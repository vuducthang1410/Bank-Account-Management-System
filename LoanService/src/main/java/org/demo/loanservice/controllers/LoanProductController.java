package org.demo.loanservice.controllers;//package org.demo.loanservice.controllers;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Schema;
//import org.demo.loanservice.common.DataResponseWrapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestController
//@RestControllerAdvice
//@Schema
//@RequestMapping("/loan-product")
//public class LoanProductController {
//
//    @Operation
//    @GetMapping("/get-all")
//    public ResponseEntity<DataResponseWrapper<Object>> getAll(
//            @RequestParam(name = "pageNumber", defaultValue = "0", required = false)
//            @Schema(description = "Page number for pagination", example = "0")
//            Integer pageNumber,
//
//            @RequestParam(name = "pageSize", defaultValue = "12", required = false)
//            @Schema(description = "Page size for pagination", example = "12")
//            Integer pageSize,
//
//            @RequestHeader(name = "transactionId")
//            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
//            String transactionId
//    ){
//
//    }
//    @Operation
//    @GetMapping("/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> getById(
//            @PathVariable(name = "id")String id,
//            @RequestHeader(name = "transactionId") String transactionId
//    ){
//        return
//    }
//    @Operation
//    @PostMapping
//    public ResponseEntity<DataResponseWrapper<Object>> save(){
//
//    }
//    @Operation
//    @PatchMapping("/active")
//    public ResponseEntity<DataResponseWrapper<Object>> active(
//            @PathVariable(name = "id")String id,
//            @RequestHeader(name = "transactionId")String transactionId
//    ){
//        return
//    }
//    @Operation
//    @PutMapping("/update/{id}")
//    public ResponseEntity<DataResponseWrapper<Object>> update(
//            @PathVariable(name = "id")String id,
//            @RequestHeader(name = "transactionId")String transactionId
//    ){
//
//    }
//
//}
