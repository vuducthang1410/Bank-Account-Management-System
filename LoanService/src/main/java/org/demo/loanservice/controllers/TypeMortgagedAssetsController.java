package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.TypeMortgagedAssetsRq;
import org.demo.loanservice.services.ITypeMortgagedAssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/type-assets")
@Schema(description = "Controller for handling operations related to Type of Mortgaged Assets")
public class TypeMortgagedAssetsController {

    private final ITypeMortgagedAssetService typeMortgagedAssetService;

    @Operation(
            summary = "Retrieve all types of mortgaged assets",
            description = "Fetches a paginated list of all types of mortgaged assets, can be filtered by page number and size."
    )
    @GetMapping("/get-all")
    public ResponseEntity<DataResponseWrapper<Object>> getAll(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false)
            @Schema(description = "Page number for pagination", example = "0")
            Integer pageNumber,

            @RequestParam(name = "pageSize", defaultValue = "12", required = false)
            @Schema(description = "Page size for pagination", example = "12")
            Integer pageSize,

            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.getAll(pageNumber, pageSize, transactionId), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a specific type of mortgaged asset by ID",
            description = "Fetches a specific type of mortgaged asset by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> getById(
            @Parameter(description = "ID of the mortgaged asset type to be retrieved")
            @PathVariable(name = "id")
            @Schema(description = "ID of the type of mortgaged asset", example = "123")
            String id,

            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.getById(id, transactionId), HttpStatus.OK);
    }

    @Operation(
            summary = "Save a new type of mortgaged asset",
            description = "Saves a new type of mortgaged asset with the given details."
    )
    @PostMapping("/save")
    public ResponseEntity<DataResponseWrapper<Object>> save(
            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId,

            @RequestBody @Valid TypeMortgagedAssetsRq typeMortgagedAssetsRq
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.save(typeMortgagedAssetsRq, transactionId), HttpStatus.OK);
    }

    @Operation(
            summary = "Activate a specific type of mortgaged asset",
            description = "Activates a specific type of mortgaged asset by its ID."
    )
    @PatchMapping("/active/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> active(
            @PathVariable(name = "id")
            @Schema(description = "ID of the type of mortgaged asset to be activated", example = "123")
            String id,

            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.active(id, transactionId), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing type of mortgaged asset",
            description = "Updates the details of an existing type of mortgaged asset identified by its ID."
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> update(
            @PathVariable(name = "id")
            @Schema(description = "ID of the type of mortgaged asset to be updated", example = "123")
            String id,

            @RequestBody @Valid TypeMortgagedAssetsRq typeMortgagedAssetsRq,

            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.update(id, typeMortgagedAssetsRq, transactionId), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a specific type of mortgaged asset",
            description = "Deletes a specific type of mortgaged asset by its ID."
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DataResponseWrapper<Object>> delete(
            @PathVariable(name = "id")
            @Schema(description = "ID of the type of mortgaged asset to be deleted", example = "123")
            String id,

            @RequestHeader(name = "transactionId")
            @Schema(description = "Unique transaction ID for the request", example = "12345abcde")
            String transactionId
    ) {
        return new ResponseEntity<>(typeMortgagedAssetService.delete(id, transactionId), HttpStatus.OK);
    }
}

