package com.revolut.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
