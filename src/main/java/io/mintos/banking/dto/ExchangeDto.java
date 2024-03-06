package io.mintos.banking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class ExchangeDto {
    private Boolean success;
    private QueryDto query;
    private BigDecimal result;
}
