package com.revolut.transfer.repository;

import com.revolut.transfer.model.Account;

import java.util.List;

public interface AccountRepository {

    Account getById(long accountId);

    Account lockAndGetById(long accountId);

    List<Account> findAllByCustomerId(long customerId);

    Account create(Account account);

    Account deactivate(Account account);

    Account deposit(Account account, long deposit);

    Account withdraw(Account account, long withdraw);

}
