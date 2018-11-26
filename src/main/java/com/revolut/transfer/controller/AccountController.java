package com.revolut.transfer.controller;

import com.revolut.transfer.dto.AccountDTO;
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
        final List<AccountDTO> customerAccountsDTO = accountService.findAllByCustomerId(customerId);
        ctx.status(200).json(customerAccountsDTO);
    };

    public static Handler create = ctx -> {
        final AccountDTO accountDTO = ctx.bodyAsClass(AccountDTO.class);
        accountDTO.setCustomerId(ctx.pathParam("customer-id"));
        final AccountDTO createdAccountDTO = accountService.create(accountDTO);
        ctx.status(201).json(createdAccountDTO);
    };

    public static Handler getOne = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final AccountDTO accountDTO = accountService.findById(accountId);
        ctx.status(200).json(accountDTO);
    };

    public static Handler deactivate = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final AccountDTO deactivatedAccountDTO = accountService.deactivate(accountId);
        ctx.status(200).json(deactivatedAccountDTO);
    };

}
