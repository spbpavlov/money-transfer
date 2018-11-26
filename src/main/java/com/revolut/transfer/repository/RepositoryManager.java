package com.revolut.transfer.repository;

/**
 * Represents a set of repositories to deal with db within one transaction.
 * {@link #commit()} must be called explicitly at the end of business method,
 * otherwise transaction will be rolled back on connection close
 */
public interface RepositoryManager extends AutoCloseable  {

    void commit();

    void rollback();

    AccountRepository getAccountRepository();

    TransferRepository getTransferRepository();

    @Override
    void close() throws RuntimeException;

}
