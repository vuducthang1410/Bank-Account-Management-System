package com.system.common_library.service;

import com.system.common_library.dto.user.CustomerDetailDTO;
import com.system.common_library.dto.user.UserDetailDTO;
import com.system.common_library.exception.DubboException;

import java.text.ParseException;
import java.util.List;

public interface CustomerDubboService {

    UserDetailDTO loadUserByToken(String token) throws DubboException, ParseException;
    List<CustomerDetailDTO> getCustomers(String firstName, String address) throws DubboException;
    CustomerDetailDTO getCustomerByCifCode(String cifCode) throws DubboException;
    List<CustomerDetailDTO> getListCustomerByCifCode(List<String> cifCode) throws DubboException;
    CustomerDetailDTO getCustomerByCustomerId(String customerId) throws DubboException;
}
