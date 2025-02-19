package com.system.common_library.service;

import com.system.common_library.dto.notifcation.BalanceFluctuationNotificationDTO;
import com.system.common_library.dto.notifcation.CustomerDetailNotiDTO;
import com.system.common_library.dto.notifcation.OTP;
import com.system.common_library.dto.response.transaction.TransactionNotificationDTO;
import com.system.common_library.exception.DubboException;

public interface NotificationDubboService {

    // Send OTP for transaction
    boolean sendOtpCodeTransaction(OTP otp, String customerCIF);

    //Send OTP code to verify by phoneNumber when creating new customer (since there was no assign email and phone number)
    boolean sendOtpCodeCustomerRegistration(OTP otp, String phoneNumber);

    //Send OTP code to verify by phoneNumber when creating new customer (since there was no assign email and phone number)
    boolean sendOtpCodeEmailCustomerRegistration(OTP otp, String email, String customerFullname);

    //Send OTP code to verify reset password for customer
    boolean sendOTPCodeCustomerResetPassword(OTP otp, CustomerDetailNotiDTO customerDetail);

    // Send transaction notification (Gửi thông báo giao dich)
    boolean sendTransactionNotification(TransactionNotificationDTO data) throws DubboException;

    //Send balance change notification to customer
    boolean sendBalanceFluctuation(BalanceFluctuationNotificationDTO balanceFluctuation);
}
