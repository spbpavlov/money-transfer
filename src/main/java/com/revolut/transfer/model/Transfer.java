package com.revolut.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transfer {

    private long id;
    private Timestamp executedTimestamp;
    private transient Account withdrawalAccount;
    private transient Currency withdrawalAccountCurrency;
    private long withdrawalAccountId;
    private long withdrawalAmount;
    private transient Account depositAccount;
    private transient Currency depositAccountCurrency;
    private long depositAccountId;
    private long depositAmount;

}

//todo check lombok @
