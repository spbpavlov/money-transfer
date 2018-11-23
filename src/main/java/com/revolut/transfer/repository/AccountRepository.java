package com.revolut.transfer.repository;

import com.revolut.transfer.model.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Iterable<Account> findAllByCustomerId(long customerId);

    Account changeBalance(Account account, long newBalance);

}
