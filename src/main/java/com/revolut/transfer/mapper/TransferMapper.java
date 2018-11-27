package com.revolut.transfer.mapper;

import com.revolut.transfer.dto.AccountOperationDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.model.Currency;
import com.revolut.transfer.model.Transfer;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final public class TransferMapper {

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

        if (Objects.nonNull(transfer.getWithdrawalAccount())) {
            transferDTO.setWithdrawalAccountId(Long.toString(transfer.getWithdrawalAccount().getId()));
            transferDTO.setWithdrawalAccountCurrency(transfer.getWithdrawalAccount().getCurrency().toString());
            transferDTO.setWithdrawalAmount(AmountMapper.longToString(transfer.getWithdrawalAmount(),
                    transfer.getWithdrawalAccount().getCurrency()));
        } else {
            transferDTO.setWithdrawalAccountId(Long.toString(transfer.getWithdrawalAccountId()));
            transferDTO.setWithdrawalAccountCurrency(transfer.getWithdrawalAccountCurrency().toString());
            transferDTO.setWithdrawalAmount(AmountMapper.longToString(transfer.getWithdrawalAmount(),
                    transfer.getWithdrawalAccountCurrency()));
        }

        if (Objects.nonNull(transfer.getDepositAccount())) {
            transferDTO.setDepositAccountId(Long.toString(transfer.getDepositAccount().getId()));
            transferDTO.setDepositAccountCurrency(transfer.getDepositAccount().getCurrency().toString());
            transferDTO.setDepositAmount(AmountMapper.longToString(transfer.getDepositAmount(),
                    transfer.getDepositAccount().getCurrency()));
        } else {
            transferDTO.setDepositAccountId(Long.toString(transfer.getDepositAccountId()));
            transferDTO.setDepositAccountCurrency(transfer.getDepositAccountCurrency().toString());
            transferDTO.setDepositAmount(AmountMapper.longToString(transfer.getDepositAmount(),
                    transfer.getDepositAccountCurrency()));

        }

        return transferDTO;

    }

    public static AccountOperationDTO transferToDepositAccountOperationDTO(@NonNull Transfer transfer) {

        AccountOperationDTO depositDTO = new AccountOperationDTO();
        depositDTO.setId(Long.toString(transfer.getId()));
        depositDTO.setExecutedTimestamp(Long.toString(transfer.getExecutedTimestamp().getTime()));
        depositDTO.setCorrespondentAccountId(Long.toString(transfer.getWithdrawalAccountId()));
        depositDTO.setAmount(AmountMapper.longToString(transfer.getDepositAmount(),
                transfer.getDepositAccountCurrency()));

        return depositDTO;

    }

    public static AccountOperationDTO transferToWithdrawalAccountOperationDTO(@NonNull Transfer transfer) {

        AccountOperationDTO withdrawalDTO = new AccountOperationDTO();
        withdrawalDTO.setId(Long.toString(transfer.getId()));
        withdrawalDTO.setExecutedTimestamp(Long.toString(transfer.getExecutedTimestamp().getTime()));
        withdrawalDTO.setCorrespondentAccountId(Long.toString(transfer.getDepositAccountId()));
        withdrawalDTO.setAmount(AmountMapper.longToString(transfer.getWithdrawalAmount(),
                transfer.getWithdrawalAccountCurrency()));

        return withdrawalDTO;

    }

    public static List<AccountOperationDTO> transferToDepositAccountOperationDTO(@NonNull List<Transfer> transfers) {

        return transfers.stream()
                .map(TransferMapper::transferToDepositAccountOperationDTO)
                .collect(Collectors.toList());

    }

    public static List<AccountOperationDTO> transferToWithdrawalAccountOperationDTO(@NonNull List<Transfer> transfers) {

        return transfers.stream()
                .map(TransferMapper::transferToWithdrawalAccountOperationDTO)
                .collect(Collectors.toList());

    }

    private TransferMapper() {}

}
