package com.revolut.transfer.controller;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.mapper.AccountMapper;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.service.AccountService;
import io.javalin.Handler;

import java.util.List;

public class AccountController extends AbstractController {

    private final static AccountService accountService;

    static {
        accountService = serviceContext.getAccountService();
    }

    public static Handler getAll = context -> {
        final long customerId = pathParamToLong(context, "customer-id");
        final List<Account> customerAccounts = accountService.findAllByCustomerId(customerId);
        context.json(AccountMapper.accountToAccountDTO(customerAccounts));
    };

    public static Handler create = context -> {
        AccountDTO accountDTO = context.bodyAsClass(AccountDTO.class);
        final long customerId = pathParamToLong(context, "customer-id");
        Account account = AccountMapper.accountDTOToAccount(accountDTO);
        account.setCustomerId(customerId);
        account = accountService.create(account);
        context.json(AccountMapper.accountToAccountDTO(account));
    };

    public static Handler getOne = context -> {
        final long accountId = pathParamToLong(context, "account-id");
        final Account account = accountService.findById(accountId);
        context.json(AccountMapper.accountToAccountDTO(account));
    };

    public static Handler getDeposits = context -> {

    };

    public static Handler getWithdrawals = context -> {

    };

    public static Handler deactivate = context -> {
        final long accountId = pathParamToLong(context, "account-id");
        final Account deactivatedAccount = accountService.deactivate(accountId);
        context.json(AccountMapper.accountToAccountDTO(deactivatedAccount));
    };

}
