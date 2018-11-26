package com.revolut.transfer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WithdrawalDTO {
    private String id;
    private String executedTimestamp;
    private String correspondentAccountId;
    private String withdrawalAmount;
}
