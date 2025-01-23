package org.demo.loanservice.controllers.exception;

public class TypeMortgagedAssetsNotFoundException extends RuntimeException {
    public TypeMortgagedAssetsNotFoundException(String id) {
        super(id);
    }
}
