package com.revolut.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Wither
public class Account {
    private long id;
    private long customerId;
    private Currency currency;
    private long balance;
    private boolean active;
}