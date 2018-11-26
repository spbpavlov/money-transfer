package com.revolut.transfer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferDTO {
    private String id;
    private String executedTimestamp;
    private String withdrawalAccountId;
    private String withdrawalAccountCurrency;
    private String withdrawalAmount;
    private String depositAccountId;
    private String depositAccountCurrency;
    private String depositAmount;
}
