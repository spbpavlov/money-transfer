package com.revolut.transfer.service;

public interface ServiceContext {

    AccountService getAccountService();

    TransferService getTransferService();

}
