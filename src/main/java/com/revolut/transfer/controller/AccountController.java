package com.revolut.transfer.controller;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.service.AccountService;
import com.revolut.transfer.service.ServiceContext;
import io.javalin.Context;

import java.util.List;

public class AccountController extends AbstractController {

    private final AccountService accountService;

    public AccountController(ServiceContext serviceContext) {
        this.accountService = serviceContext.getAccountService();
    }

    public void create(Context ctx) {
        final AccountDTO accountDTO = ctx.bodyAsClass(AccountDTO.class);
        accountDTO.setCustomerId(ctx.pathParam("customer-id"));
        final AccountDTO createdAccountDTO = this.accountService.create(accountDTO);
        ctx.status(201).json(createdAccountDTO);
    }

    public void getOne(Context ctx) {
        final long accountId = pathParamToLong(ctx, "account-id");
        final AccountDTO accountDTO = this.accountService.getById(accountId);
        ctx.status(200).json(accountDTO);
    }

    public void deactivate(Context ctx) {
        final long accountId = pathParamToLong(ctx, "account-id");
        final AccountDTO deactivatedAccountDTO = this.accountService.deactivate(accountId);
        ctx.status(200).json(deactivatedAccountDTO);
    }

    public void getAll(Context ctx) {
        final long customerId = pathParamToLong(ctx, "customer-id");
        final List<AccountDTO> customerAccountsDTO = this.accountService.findAllByCustomerId(customerId);
        ctx.status(200).json(customerAccountsDTO);
    }

}
