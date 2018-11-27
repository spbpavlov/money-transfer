package com.revolut.transfer.service;

import com.revolut.transfer.dto.AccountOperationDTO;
import com.revolut.transfer.dto.TransferDTO;

import java.sql.Timestamp;
import java.util.List;

public interface TransferService {

    TransferDTO transfer(TransferDTO transfer);

    List<AccountOperationDTO> getDeposits(long accountId, Timestamp start, Timestamp end);

    List<AccountOperationDTO> getWithdrawals(long accountId, Timestamp start, Timestamp end);

}
