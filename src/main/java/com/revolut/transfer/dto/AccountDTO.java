package com.revolut.transfer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDTO {
    private String id;
    private String customerId;
    private String currency;
    private String balance;
    private String active;
}
