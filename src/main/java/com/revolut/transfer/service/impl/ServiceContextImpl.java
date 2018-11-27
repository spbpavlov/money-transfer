package com.revolut.transfer.service.impl;

import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.service.AccountService;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.TransferService;

public class ServiceContextImpl implements ServiceContext {

    private final AccountService accountService;
    private final TransferService transferService;

    public ServiceContextImpl(RepositoryManagerFactory repositoryManagerFactory) {
        this.accountService = new AccountServiceImpl(repositoryManagerFactory);
        this.transferService = new TransferServiceImpl(repositoryManagerFactory);
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
