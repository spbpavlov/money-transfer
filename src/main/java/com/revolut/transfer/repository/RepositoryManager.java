package com.revolut.transfer.repository;

public interface RepositoryManager extends AutoCloseable  {

    void commitTransaction();

    void rollbackTransaction();

    AccountRepository getAccountRepository();

    TransferRepository getTransferRepository();

    @Override
    void close() throws RuntimeException;

}
