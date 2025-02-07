package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.request.FormDeftRepaymentRq;
import org.demo.loanservice.dto.response.FormDeftRepaymentRp;
import org.demo.loanservice.entities.FormDeftRepayment;
import org.demo.loanservice.repositories.FormDeftRepaymentRepository;
import org.demo.loanservice.services.IFormDeftRepaymentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormDeftRepaymentServiceImpl implements IFormDeftRepaymentService {

    private final FormDeftRepaymentRepository formDeftRepaymentRepository;
    private final Logger log = LogManager.getLogger(FormDeftRepaymentServiceImpl.class);
    private final Util util;

    @Override
    public DataResponseWrapper<Object> save(FormDeftRepaymentRq formDeftRepaymentRq, String transactionId) {
        FormDeftRepayment formDeftRepayment = new FormDeftRepayment();
        formDeftRepayment.setDescription(formDeftRepaymentRq.getDescription());
        formDeftRepayment.setName(formDeftRepaymentRq.getFormName());
        formDeftRepayment.setCode(FormDeftRepaymentEnum.valueOf(formDeftRepaymentRq.getCode()));
        formDeftRepayment.setIsActive(false);
        formDeftRepayment.setIsDeleted(false);
        formDeftRepaymentRepository.save(formDeftRepayment);
        return DataResponseWrapper.builder()
                .data(Map.of("FormDeftRepaymentId", formDeftRepayment.getId()))
                .message(util.getMessageFromMessageSource(MessageValue.CREATED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    @Override
    @Cacheable(value = "form_deft_repayment", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        FormDeftRepayment formDeftRepayment=getFormDeftRepayment(id);
        FormDeftRepaymentRp formDeftRepaymentRp = convertToRp(formDeftRepayment);
        return DataResponseWrapper.builder()
                .data(formDeftRepaymentRp)
                .message(util.getMessageFromMessageSource(MessageValue.CREATED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<FormDeftRepayment> formDeftRepaymentPage = formDeftRepaymentRepository
                .findAllByIsDeleted(false, pageable);
        Map<String, Object> dataResponse = new HashMap<>();
        List<FormDeftRepaymentRp> formDeftRepaymentRpList = formDeftRepaymentPage.getContent()
                .stream().map(this::convertToRp).toList();
        dataResponse.put("totalRecords", formDeftRepaymentPage.getTotalElements());
        dataResponse.put("values", formDeftRepaymentRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .status("00000")
                .message(util.getMessageFromMessageSource(MessageValue.FIND_SUCCESSFULLY))
                .build();
    }

    @Override
    @CacheEvict(value = "form_deft_repayment", key = "#id")
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        FormDeftRepayment formDeftRepayment=getFormDeftRepayment(id);
        formDeftRepayment.setIsActive(!formDeftRepayment.getIsActive());
        formDeftRepaymentRepository.save(formDeftRepayment);
        return DataResponseWrapper.builder()
                .data(convertToRp(formDeftRepayment))
                .message(util.getMessageFromMessageSource(MessageValue.CREATED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    private FormDeftRepayment getFormDeftRepayment(String id) {
        Optional<FormDeftRepayment> optionalFormDeftRepayment = formDeftRepaymentRepository.findByIdAndIsDeleted(id, false);
        if (optionalFormDeftRepayment.isEmpty()) {
            log.info(MessageData.FORM_DEFT_REPAYMENT_NOT_FOUNT.getMessageLog());
            throw new DataNotFoundException(MessageData.FORM_DEFT_REPAYMENT_NOT_FOUNT.getKeyMessage(), MessageData.FORM_DEFT_REPAYMENT_NOT_FOUNT.getCode());
        }
        return optionalFormDeftRepayment.get();
    }

    @Override
    public DataResponseWrapper<Object> update(String id, FormDeftRepaymentRq formDeftRepaymentRq, String transactionId) {
        return null;
    }

    @Override
    @CacheEvict(value = "form_deft_repayment", key = "#id")
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        FormDeftRepayment formDeftRepayment=getFormDeftRepayment(id);
        formDeftRepayment.setIsDeleted(true);
        formDeftRepaymentRepository.save(formDeftRepayment);
        return DataResponseWrapper.builder()
                .data(Map.of("FormDeftRepaymentId", formDeftRepayment.getId()))
                .message(util.getMessageFromMessageSource(MessageValue.DELETED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    private FormDeftRepaymentRp convertToRp(FormDeftRepayment formDeftRepayment) {
        FormDeftRepaymentRp formDeftRepaymentRp = new FormDeftRepaymentRp();
        formDeftRepaymentRp.setFormId(formDeftRepayment.getId());
        formDeftRepaymentRp.setFormName(formDeftRepayment.getName());
        formDeftRepaymentRp.setIsActive(formDeftRepayment.getIsActive());
        formDeftRepaymentRp.setCode(formDeftRepayment.getCode().name());
        return formDeftRepaymentRp;
    }
}
