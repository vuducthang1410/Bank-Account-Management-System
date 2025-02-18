package org.demo.loanservice.services.impl;

import com.system.common_library.dto.report.LoanReportDTO;
import com.system.common_library.dto.report.LoanReportRequest;
import com.system.common_library.dto.report.LoanReportResponse;
import com.system.common_library.exception.DubboException;
import com.system.common_library.service.LoanDubboService;

import java.util.List;

public class LoanDubboServiceImpl implements LoanDubboService {

    @Override
    public void getLoanDetail(String account) {

    }

    @Override
    public void getLoanList(String cifCode) {

    }

    @Override
    public LoanReportResponse getLoanReport(String loanId) throws DubboException {
        return null;
    }

    @Override
    public List<LoanReportDTO> getListLoanByField(LoanReportRequest request) throws DubboException {
        return List.of();
    }
}