package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.TransferRepository;
import org.sql2o.Connection;

public class TransferRepositoryImpl implements TransferRepository {

    private final Connection con;

    TransferRepositoryImpl(Connection con) {
        this.con = con;
    }

    @Override
    public void transfer(Account from, Account to, long amount) {

    }

}
