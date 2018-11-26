package com.revolut.transfer.controller;

import com.revolut.transfer.dto.DepositDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.dto.WithdrawalDTO;
import com.revolut.transfer.service.TransferService;
import io.javalin.Handler;

import java.sql.Timestamp;
import java.util.List;

public class TransferController extends AbstractController {

    private final static TransferService transferService;

    static {
        transferService = serviceContext.getTransferService();
    }

    public static Handler create = ctx -> {
        final TransferDTO transferDTO = ctx.bodyAsClass(TransferDTO.class);
        final TransferDTO executedTransferDTO = transferService.transfer(transferDTO);
        ctx.status(201).json(executedTransferDTO);
    };

    public static Handler getDeposits = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Timestamp start = queryParamToTimestamp(ctx, "start");
        final Timestamp end = queryParamToTimestamp(ctx, "end");
        final List<DepositDTO> depositsDTO = transferService.getDeposits(accountId, start, end);
        ctx.status(200).json(depositsDTO);
    };

    public static Handler getWithdrawals = ctx -> {
        final long accountId = pathParamToLong(ctx, "account-id");
        final Timestamp start = queryParamToTimestamp(ctx, "start");
        final Timestamp end = queryParamToTimestamp(ctx, "end");
        final List<WithdrawalDTO> withdrawalsDTO = transferService.getWithdrawals(accountId, start, end);
        ctx.status(200).json(withdrawalsDTO);
    };

}
