package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.controllers.exception.TypeMortgagedAssetsNotFoundException;
import org.demo.loanservice.dto.enumDto.AssetStatus;
import org.demo.loanservice.dto.enumDto.AssetType;
import org.demo.loanservice.dto.request.TypeMortgagedAssetsRq;
import org.demo.loanservice.dto.response.TypeMortgagedAssetsRp;
import org.demo.loanservice.entities.TypeMortgagedAssets;
import org.demo.loanservice.repositories.TypeMortgagedAssetRepository;
import org.demo.loanservice.services.ITypeMortgagedAssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeMortgagedAssetServiceImpl implements ITypeMortgagedAssetService {
    private final TypeMortgagedAssetRepository typeMortgagedAssetRepository;

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate"));
        Page<TypeMortgagedAssets> typeMortgagedAssetsPage = typeMortgagedAssetRepository.getAllByIsDeleted(false, pageable);
        Map<String, Object> dataResponse = new HashMap<>();
        List<TypeMortgagedAssetsRp> listData = typeMortgagedAssetsPage.getContent()
                .stream()
                .map(this::convertToTypeMortgagedAssetsRp)
                .toList();
        dataResponse.put("totalRecord", typeMortgagedAssetsPage.getTotalElements());
        dataResponse.put("listData", listData);
        return DataResponseWrapper.createDataResponseWrapper(dataResponse, "success", "00000");
    }

    @Override
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        Optional<TypeMortgagedAssets> optionalTypeMortgagedAssets = typeMortgagedAssetRepository.getTypeMortgagedAssetsByIdAndIsDeleted(id, false);
        if (optionalTypeMortgagedAssets.isEmpty()) {
            throw new TypeMortgagedAssetsNotFoundException(id);
        }
        TypeMortgagedAssets typeMortgagedAssets = optionalTypeMortgagedAssets.get();
        return DataResponseWrapper.createDataResponseWrapper(convertToTypeMortgagedAssetsRp(typeMortgagedAssets), "successfully", "00000");
    }

    @Override
    public DataResponseWrapper<Object> save(TypeMortgagedAssetsRq typeMortgagedAssetsRq, String transactionId) {
        TypeMortgagedAssets typeMortgagedAssets=new TypeMortgagedAssets();
        typeMortgagedAssets.setAssetStatus(AssetStatus.valueOf(typeMortgagedAssetsRq.getStatus()));
        typeMortgagedAssets.setAssetType(AssetType.valueOf(typeMortgagedAssetsRq.getTypeAssets()));
        typeMortgagedAssets.setDescription(typeMortgagedAssetsRq.getDescription());
        typeMortgagedAssets.setName(typeMortgagedAssetsRq.getName());
        typeMortgagedAssets.setIsActive(false);
        typeMortgagedAssets.setIsDeleted(false);
        typeMortgagedAssetRepository.save(typeMortgagedAssets);
        return  DataResponseWrapper.createDataResponseWrapper(convertToTypeMortgagedAssetsRp(typeMortgagedAssets), "successfully", "00000");
    }

    @Override
    public DataResponseWrapper<Object> update(String id,TypeMortgagedAssetsRq typeMortgagedAssetsRq, String transactionId) {
        Optional<TypeMortgagedAssets> optionalTypeMortgagedAssets = typeMortgagedAssetRepository.getTypeMortgagedAssetsByIdAndIsDeleted(id, false);
        if (optionalTypeMortgagedAssets.isEmpty()) {
            throw new TypeMortgagedAssetsNotFoundException(id);
        }
        TypeMortgagedAssets typeMortgagedAssets=optionalTypeMortgagedAssets.get();
        typeMortgagedAssets.setAssetStatus(AssetStatus.valueOf(typeMortgagedAssetsRq.getStatus()));
        typeMortgagedAssets.setAssetType(AssetType.valueOf(typeMortgagedAssetsRq.getTypeAssets()));
        typeMortgagedAssets.setDescription(typeMortgagedAssetsRq.getDescription());
        typeMortgagedAssets.setName(typeMortgagedAssetsRq.getName());
        typeMortgagedAssetRepository.save(typeMortgagedAssets);
        return  DataResponseWrapper.createDataResponseWrapper(convertToTypeMortgagedAssetsRp(typeMortgagedAssets), "successfully", "00000");
    }

    @Override
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        Optional<TypeMortgagedAssets> optionalTypeMortgagedAssets = typeMortgagedAssetRepository.getTypeMortgagedAssetsByIdAndIsDeleted(id, false);
        if (optionalTypeMortgagedAssets.isEmpty()) {
            throw new TypeMortgagedAssetsNotFoundException(id);
        }
        TypeMortgagedAssets typeMortgagedAssets=optionalTypeMortgagedAssets.get();
        typeMortgagedAssets.setIsActive(!typeMortgagedAssets.getIsActive());
        typeMortgagedAssetRepository.save(typeMortgagedAssets);
        return  DataResponseWrapper.createDataResponseWrapper(null, "successfully", "00000");
    }

    @Override
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        Optional<TypeMortgagedAssets> optionalTypeMortgagedAssets = typeMortgagedAssetRepository.getTypeMortgagedAssetsByIdAndIsDeleted(id, false);
        if (optionalTypeMortgagedAssets.isEmpty()) {
            throw new TypeMortgagedAssetsNotFoundException(id);
        }
        TypeMortgagedAssets typeMortgagedAssets=optionalTypeMortgagedAssets.get();
        typeMortgagedAssets.setIsDeleted(true);
        typeMortgagedAssetRepository.save(typeMortgagedAssets);
        return  DataResponseWrapper.createDataResponseWrapper(null, "successfully", "00000");
    }

    private TypeMortgagedAssetsRp convertToTypeMortgagedAssetsRp(TypeMortgagedAssets typeMortgagedAssets) {
        TypeMortgagedAssetsRp typeMortgagedAssetsRp = new TypeMortgagedAssetsRp();
        typeMortgagedAssetsRp.setId(typeMortgagedAssets.getId());
        typeMortgagedAssetsRp.setName(typeMortgagedAssets.getName());
        typeMortgagedAssetsRp.setAssetType(String.valueOf(typeMortgagedAssets.getAssetType()));
        typeMortgagedAssetsRp.setAssetStatus(String.valueOf(typeMortgagedAssets.getAssetStatus()));
        typeMortgagedAssetsRp.setDescription(typeMortgagedAssets.getDescription());
        typeMortgagedAssetsRp.setIsActive(typeMortgagedAssets.getIsActive());
        typeMortgagedAssetsRp.setCreatedDate(DateUtil.format(
                DateUtil.DD_MM_YYY_HH_MM_SLASH,
                Date.from(typeMortgagedAssets.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()))
        );
        return typeMortgagedAssetsRp;
    }
}
