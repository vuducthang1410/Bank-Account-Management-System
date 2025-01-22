package org.demo.loanservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataResponseWrapper <T>{
    private String status;
    private String message;
    private T data;
}
