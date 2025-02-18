package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.services.ILoanDetailRepaymentScheduleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanDetailRepaymentScheduleServiceImpl implements ILoanDetailRepaymentScheduleService {
    private final LoanDetailInfoRepository loanDetailInfoRepository;
    private final Logger log= LogManager.getLogger(LoanDetailRepaymentScheduleServiceImpl.class);
    @Override
    public LoanDetailInfo getLoanDetailInfoById(String loanDetailInfoId, String transactionId) {
        return loanDetailInfoRepository
                .findByIdAndIsDeleted(loanDetailInfoId, false)
                .orElseThrow(() -> {
                    log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_DETAIL_INFO_NOT_FOUND, loanDetailInfoId, transactionId);
                    return new DataNotValidException(MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getKeyMessage(),
                            MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getCode());
                });
    }
}
