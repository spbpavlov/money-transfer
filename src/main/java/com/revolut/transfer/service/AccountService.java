package com.revolut.transfer.service;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.dto.TransferDTO;

import java.util.List;

public interface AccountService {

    List<AccountDTO> findAllByCustomerId(long customerId);

    AccountDTO create(AccountDTO account);

    AccountDTO findById(long accountId);

    AccountDTO deactivate(long accountId);

}
