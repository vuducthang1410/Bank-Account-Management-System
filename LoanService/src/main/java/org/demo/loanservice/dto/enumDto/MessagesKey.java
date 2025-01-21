package org.demo.loanservice.dto.enumDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessagesKey {
    INVALID_DATA("invalid.data");
    private final String key;
}
