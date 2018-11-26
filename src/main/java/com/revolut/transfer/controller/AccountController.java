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

    public static Handler getAll = ctx -> {
        final long customerId = pathParamToLong(ctx, "customer-id");
        final List<Account> customerAccounts = accountService.findAllByCustomerId(customerId);
        ctx.status(200).json(AccountMapper.accountToAccountDTO(customerAccounts));
    };

    public static Handler create = ctx -> {
        AccountDTO accountDTO = ctx.bodyAsClass(AccountDTO.class);
        final long customerId = pathParamToLong(ctx, "customer-id");
        Account account = AccountMapper.accountDTOToAccount(accountDTO);
        account.setCustomerId(customerId);
        account = accountService.create(account);
        ctx.status(201).json(AccountMapper.accountToAccountDTO(account));
    };

    public static Handler getOne = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Account account = accountService.findById(accountId);
        ctx.status(200).json(AccountMapper.accountToAccountDTO(account));
    };

    public static Handler getDeposits = ctx -> {

    };

    public static Handler getWithdrawals = ctx -> {

    };

    public static Handler deactivate = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Account deactivatedAccount = accountService.deactivate(accountId);
        ctx.status(200).json(AccountMapper.accountToAccountDTO(deactivatedAccount));
    };

}
