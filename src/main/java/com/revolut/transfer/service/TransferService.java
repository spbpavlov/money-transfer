package com.revolut.transfer.service;

import com.revolut.transfer.dto.DepositDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.dto.WithdrawalDTO;

import java.sql.Timestamp;
import java.util.List;

public interface TransferService {

    TransferDTO transfer(TransferDTO transfer);

    List<DepositDTO> getDeposits(long accountId, Timestamp start, Timestamp end);

    List<WithdrawalDTO> getWithdrawals(long accountId, Timestamp start, Timestamp end);

}
