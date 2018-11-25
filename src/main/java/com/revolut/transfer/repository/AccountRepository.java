package com.revolut.transfer.repository;

import com.revolut.transfer.model.Account;

import java.util.List;

public interface AccountRepository {

    Account findById(long var1, boolean forUpdate);

    List<Account> findAllById(Iterable<Long> var1, boolean forUpdate);

    List<Account> findAllByCustomerId(long customerId, boolean forUpdate);

    Account create(Account account);

    Account deactivate(Account account);

    Account deposit(Account account, long deposit);

    Account withdraw(Account account, long withdraw);

}
