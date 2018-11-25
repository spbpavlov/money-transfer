package com.revolut.transfer.service.impl;

import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.impl.RepositoryManagerFactoryImpl;
import com.revolut.transfer.service.AccountService;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.TransferService;

public class ServiceContextImpl implements ServiceContext {

    private static ServiceContextImpl ourInstance = new ServiceContextImpl();

    public static ServiceContextImpl getInstance() {
        return ourInstance;
    }

    private final AccountService accountService;
    private final TransferService transferService;

    private ServiceContextImpl() {
        final RepositoryManagerFactory repositoryTransactionContextFactory
                = new RepositoryManagerFactoryImpl();
        this.accountService = new AccountServiceImpl(repositoryTransactionContextFactory);
        this.transferService = new TransferServiceImpl(repositoryTransactionContextFactory);
    }

    @Override
    public AccountService getAccountService() {
        return this.accountService;
    }

    @Override
    public TransferService getTransferService() {
        return this.transferService;
    }

}
