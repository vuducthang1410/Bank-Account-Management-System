package org.demo.loanservice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponseWrapper <T>{
    private T data;
    private String message;
    private String status;
}