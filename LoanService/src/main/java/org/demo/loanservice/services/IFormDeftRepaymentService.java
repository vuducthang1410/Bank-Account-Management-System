package org.demo.loanservice.services;

import org.demo.loanservice.dto.request.FormDeftRepaymentRq;
import org.demo.loanservice.entities.FormDeftRepayment;

public interface IFormDeftRepaymentService extends  IBaseService<FormDeftRepaymentRq>{
    FormDeftRepayment getFormDeftRepaymentById(String id, String transactionId);
}
