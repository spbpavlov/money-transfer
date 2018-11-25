package com.revolut.transfer.mapper;

import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Currency;
import com.revolut.transfer.model.Transfer;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class TransferMapper {

    public static Transfer transferDTOtoTransfer(@NonNull TransferDTO transferDTO) {

        Transfer transfer = new Transfer();

        transfer.setId(Objects.isNull(transferDTO.getId()) ? 0 : Long.parseLong(transferDTO.getId()));
        transfer.setExecutedTimestamp(Objects.isNull(transferDTO.getExecutedTimestamp())
                ? new Timestamp(System.currentTimeMillis())
                : new Timestamp(Long.parseLong(transferDTO.getExecutedTimestamp()))
        );

        transfer.setWithdrawalAccountId(Long.parseLong(transferDTO.getWithdrawalAccountId()));
        transfer.setWithdrawalAccountCurrency(Currency.valueOf(transferDTO.getWithdrawalAccountCurrency()));
        transfer.setWithdrawalAmount(AmountMapper.stringToLong(transferDTO.getWithdrawalAmount(),
                transfer.getWithdrawalAccountCurrency()));

        transfer.setDepositAccountId(Long.parseLong(transferDTO.getDepositAccountId()));
        transfer.setDepositAccountCurrency(Currency.valueOf(transferDTO.getDepositAccountCurrency()));
        transfer.setDepositAmount(AmountMapper.stringToLong(transferDTO.getDepositAmount(),
                transfer.getDepositAccountCurrency()));

        return transfer;

    }

    public static TransferDTO transferToTransferDTO(@NonNull Transfer transfer) {

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setId(Long.toString(transfer.getId()));
        transferDTO.setExecutedTimestamp(Long.toString(transfer.getExecutedTimestamp().getTime()));

        transferDTO.setWithdrawalAccountId(Long.toString(transfer.getWithdrawalAccount().getId()));
        transferDTO.setWithdrawalAccountCurrency(transfer.getWithdrawalAccount().getCurrency().toString());
        transferDTO.setWithdrawalAmount(AmountMapper.longToString(transfer.getWithdrawalAmount(),
                transfer.getWithdrawalAccount().getCurrency()));

        transferDTO.setDepositAccountId(Long.toString(transfer.getDepositAccount().getId()));
        transferDTO.setDepositAccountCurrency(transfer.getDepositAccount().getCurrency().toString());
        transferDTO.setDepositAmount(AmountMapper.longToString(transfer.getDepositAmount(),
                transfer.getDepositAccount().getCurrency()));

        return transferDTO;

    }

    public static void mapAccounts(@NonNull Transfer transfer, @NonNull List<Account> accounts) {

        for (Account account : accounts) {
            if (account.getId() ==  transfer.getWithdrawalAccountId()) {
                transfer.setWithdrawalAccount(account);
            } else if (account.getId() ==  transfer.getDepositAccountId()) {
                transfer.setDepositAccount(account);
            }
        }

    }

}
