package org.demo.loanservice.dto;

import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.dto.response.InterestRateRp;
import org.demo.loanservice.entities.InterestRate;

import java.time.ZoneId;
import java.util.Date;
public class MapToDto {
    public static InterestRateRp convertToInterestRateRp(InterestRate interestRate) {
        InterestRateRp interestRateRp = new InterestRateRp();
        interestRateRp.setId(interestRate.getId());
        interestRateRp.setInterestRate(interestRate.getInterestRate().toString());
        interestRateRp.setUnit(interestRate.getUnit().name());
        interestRateRp.setIsActive(interestRate.getIsActive().toString());
        interestRateRp.setMinimumAmount(interestRate.getMinimumAmount().stripTrailingZeros().toPlainString());
        interestRateRp.setMinimumLoanTerm(interestRate.getMinimumLoanTerm().toString());
        interestRateRp.setDateActive(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, new Date(interestRate.getDateActive().getTime())));
        interestRateRp.setCreatedDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, Date.from(interestRate.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant())));
        return interestRateRp;
    }
}
