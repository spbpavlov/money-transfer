package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.db.DataSourceFactory;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.TransferRepository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Objects;

/**
 * Represents a set of repositories to deal with db within one transaction.
 * {@link #commit()} must be called explicitly at the end of business method,
 * otherwise transaction will be rolled back on connection close.
 * The RepositoryManager is not intended for sharing between threads, so no synchronization is implemented.
 */
public class RepositoryManagerImpl implements RepositoryManager {

    private static final Sql2o sql2o;

    private final Connection con;
    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    static {
        sql2o = new Sql2o(DataSourceFactory.getDataSource());
    }

    public RepositoryManagerImpl(int isolationLevel) {
        this.con = sql2o.beginTransaction(isolationLevel);
        this.con.setRollbackOnClose(true);
        this.con.setRollbackOnException(true);
    }

    @Override
    public void commit() {
        this.con.commit();
    }

    @Override
    public void rollback() {
        this.con.rollback();
    }

    /**
     * The RepositoryManager is not intended for sharing between threads, so no synchronization is implemented.
     */
    @Override
    public AccountRepository getAccountRepository() {
        if (Objects.isNull(this.accountRepository)) {
            this.accountRepository = new AccountRepositoryImpl(con);
        }
        return this.accountRepository;
    }

    /**
     * The RepositoryManager is not intended for sharing between threads, so no synchronization is implemented.
     */
    @Override
    public TransferRepository getTransferRepository() {
        if (Objects.isNull(this.transferRepository)) {
            this.transferRepository = new TransferRepositoryImpl(con);
        }
        return this.transferRepository;
    }

    @Override
    public void close() throws RuntimeException {
        con.close();
    }

}
