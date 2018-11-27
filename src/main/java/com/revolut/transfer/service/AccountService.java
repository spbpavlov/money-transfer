package com.revolut.transfer.service;

import com.revolut.transfer.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    List<AccountDTO> findAllByCustomerId(long customerId);

    AccountDTO create(AccountDTO account);

    AccountDTO getById(long accountId);

    AccountDTO deactivate(long accountId);

}
