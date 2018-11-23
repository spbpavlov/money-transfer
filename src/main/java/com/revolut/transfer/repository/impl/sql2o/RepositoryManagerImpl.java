package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.db.DataSourceFactory;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.TransferRepository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Objects;

public class RepositoryManagerImpl implements RepositoryManager {

    private static final Sql2o sql2o;
    private final Connection con;
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    static {
        sql2o = new Sql2o(DataSourceFactory.getDataSource());
    }

    public RepositoryManagerImpl(int isolationLevel) {
        this.con = sql2o.beginTransaction(isolationLevel);
        this.accountRepository = new AccountRepositoryImpl(con);
        this.transferRepository = new TransferRepositoryImpl(con);
    }

    @Override
    public void commitTransaction() {
        this.con.commit();
    }

    @Override
    public void rollbackTransaction() {
        this.con.rollback();
    }

    @Override
    public AccountRepository getAccountRepository() {
        return this.accountRepository;
    }

    @Override
    public TransferRepository getTransferRepository() {
        return this.transferRepository;
    }

    @Override
    public void close() throws RuntimeException {
        con.close();
    }

}
