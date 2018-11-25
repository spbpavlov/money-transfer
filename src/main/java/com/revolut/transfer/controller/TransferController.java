package com.revolut.transfer.controller;

import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.mapper.TransferMapper;
import com.revolut.transfer.model.Transfer;
import com.revolut.transfer.service.TransferService;
import io.javalin.Handler;

public class TransferController extends AbstractController {

    private final static TransferService transferService;

    static {
        transferService = serviceContext.getTransferService();
    }

    public static Handler create = context -> {
        TransferDTO transferDTO = context.bodyAsClass(TransferDTO.class);
        final Transfer transfer = TransferMapper.transferDTOtoTransfer(transferDTO);
        transferService.transfer(transfer);
    };

}
