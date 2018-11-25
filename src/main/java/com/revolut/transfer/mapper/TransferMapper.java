package com.revolut.transfer.mapper;

import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Currency;
import com.revolut.transfer.model.Transfer;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.List;

public class TransferMapper {

    public static Transfer transferDTOtoTransfer(@NonNull TransferDTO transferDTO) {

        Transfer transfer = new Transfer();
        transfer.setExecutedTimestamp(new Timestamp(System.currentTimeMillis()));

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
