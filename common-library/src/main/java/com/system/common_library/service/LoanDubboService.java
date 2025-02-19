package com.system.common_library.service;

import com.system.common_library.dto.report.LoanReportDTO;
import com.system.common_library.dto.report.LoanReportRequest;
import com.system.common_library.dto.report.LoanReportResponse;
import com.system.common_library.exception.DubboException;

import java.util.List;

public interface LoanDubboService {

    void getLoanDetail(String account);

    void getLoanList(String cifCode);

    LoanReportResponse getLoanReport(String loanId) throws DubboException;
    List<LoanReportDTO> getListLoanByField(LoanReportRequest request) throws DubboException;
}
