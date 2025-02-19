package com.system.common_library.dto.user;

import com.system.common_library.enums.ObjectStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDetailDTO implements Serializable {
    String customerId;
    String cifCode;
    String phone;
    String address;
    LocalDate dob;
    String mail;
    String fullName;
    String firstName;
    String lastName;
    String identityCard;
    String gender;
    boolean isActive;
    ObjectStatus status;
    String customerNumber;
}

