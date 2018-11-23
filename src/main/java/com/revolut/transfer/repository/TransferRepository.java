package com.revolut.transfer.repository;

import com.revolut.transfer.model.Account;

public interface TransferRepository {

    void transfer(Account from, Account to, long amount);

}
