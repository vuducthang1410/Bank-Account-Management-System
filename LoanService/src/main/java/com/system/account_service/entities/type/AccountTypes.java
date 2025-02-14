package com.system.account_service.entities.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypes {
    PAYMENT(Values.PAYMENT),
    CREDIT(Values.CREDIT),
    SAVING(Values.SAVING),
    LOAN(Values.LOAN);

    private final String value;

    public static class Values {
        public static final String PAYMENT= "PAYMENT";
        public static final String CREDIT= "CREDIT";
        public static final String SAVING= "SAVING";
        public static final String LOAN= "LOAN";
    }
}
