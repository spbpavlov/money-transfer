package com.revolut.transfer.controller;

import com.revolut.transfer.dto.DepositDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.dto.WithdrawalDTO;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.TransferService;
import io.javalin.Context;

import java.sql.Timestamp;
import java.util.List;

public class TransferController extends AbstractController {

    private final TransferService transferService;

    public TransferController(ServiceContext serviceContext) {
        transferService = serviceContext.getTransferService();
    }

    public void create(Context ctx) {
        final TransferDTO transferDTO = ctx.bodyAsClass(TransferDTO.class);
        final TransferDTO executedTransferDTO = transferService.transfer(transferDTO);
        ctx.status(201).json(executedTransferDTO);
    }

    public void getDeposits(Context ctx) {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Timestamp start = queryParamToTimestamp(ctx, "start");
        final Timestamp end = queryParamToTimestamp(ctx, "end");
        final List<DepositDTO> depositsDTO = transferService.getDeposits(accountId, start, end);
        ctx.status(200).json(depositsDTO);
    }

    public void getWithdrawals(Context ctx) {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Timestamp start = queryParamToTimestamp(ctx, "start");
        final Timestamp end = queryParamToTimestamp(ctx, "end");
        final List<WithdrawalDTO> withdrawalsDTO = transferService.getWithdrawals(accountId, start, end);
        ctx.status(200).json(withdrawalsDTO);
    }

}
