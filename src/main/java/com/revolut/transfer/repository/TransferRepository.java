package com.revolut.transfer.repository;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Transfer;

import java.sql.Timestamp;
import java.util.List;

public interface TransferRepository {

    Transfer create(Transfer transfer);

    List<Transfer> findWithdrawalByAccountId(long accountId, Timestamp start, Timestamp end);

    List<Transfer> findDepositByAccountId(long accountId, Timestamp start, Timestamp end);

}
