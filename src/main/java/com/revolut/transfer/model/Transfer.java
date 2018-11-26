package com.revolut.transfer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Transfer {

    private long id;
    private Timestamp executedTimestamp;
    private Account withdrawalAccount;
    private Currency withdrawalAccountCurrency;
    private long withdrawalAccountId;
    private long withdrawalAmount;
    private Account depositAccount;
    private Currency depositAccountCurrency;
    private long depositAccountId;
    private long depositAmount;

}