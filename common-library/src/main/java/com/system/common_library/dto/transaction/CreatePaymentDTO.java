package com.system.common_library.dto.transaction;

import com.system.common_library.enums.*;
import com.system.common_library.util.Constant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDTO implements Serializable {

    @NotNull(message = "{" + Constant.CIF_CODE_REQUIRE + "}")
    @Size(min = 8, max = 11, message = "{" + Constant.CIF_CODE_SIZE + "}")
    private String cifCode;

    @Email(regexp = "^(.+)@(\\S+)$", message = "{" + Constant.INVALID_FORMAT_EMAIL + "}")
    private String email;

    private String customerId;

    private String customerName;

    private String customerPhone;

    private String providerName;

    private String providerCode;

    private String invoiceNumber;

    private String promotionCode;

    private String qrCode;

    private String paymentCode;

    private LocalDateTime issueDate;

    private LocalDateTime dueDate;

    private String senderAccountId;

    @NotNull(message = "{" + Constant.SENDER_ACCOUNT_REQUIRE + "}")
    @Size(min = 2, max = 255, message = "{" + Constant.SENDER_ACCOUNT_SIZE + "}")
    private String senderAccount;

    private AccountType senderAccountType;

    @NotNull(message = "{" + Constant.SENDER_NAME_REQUIRE + "}")
    @Size(min = 2, max = 255, message = "{" + Constant.SENDER_NAME_SIZE + "}")
    private String senderAccountName;

    private String receiverAccountId;

    @NotNull(message = "{" + Constant.RECEIVER_ACCOUNT_REQUIRE + "}")
    @Size(min = 2, max = 255, message = "{" + Constant.RECEIVER_ACCOUNT_SIZE + "}")
    private String receiverAccount;

    private AccountType receiverAccountType;

    @NotNull(message = "{" + Constant.RECEIVER_NAME_REQUIRE + "}")
    @Size(min = 2, max = 255, message = "{" + Constant.RECEIVER_NAME_SIZE + "}")
    private String receiverAccountName;

    @Range(min = 1, max = 1000000000, message = "{" + Constant.INVALID_AMOUNT + "}")
    private BigDecimal amount;

    @Range(min = 0, max = 1000000000, message = "{" + Constant.INVALID_FEE + "}")
    private BigDecimal fee;

    private FeePayer feePayer;

    private String note;

    private Initiator initiator;

    private Method method;

    private Type type;

    private String description;
}
