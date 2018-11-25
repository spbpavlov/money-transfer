package com.revolut.transfer.service;

import com.revolut.transfer.model.Account;

import java.util.List;

public interface AccountService {

    List<Account> findAllByCustomerId(long customerId);

    Account create(Account account);

    Account findById(Long id);

    Account deactivate(Long id);

}
