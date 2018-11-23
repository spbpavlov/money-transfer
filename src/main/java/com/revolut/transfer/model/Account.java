package com.revolut.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private long id;
    private long customerId;
    private Currency currency;
    private long balance;
    private boolean active;
}
