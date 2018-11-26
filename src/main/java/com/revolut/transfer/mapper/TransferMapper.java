package com.revolut.transfer.mapper;

import com.revolut.transfer.dto.DepositDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.dto.WithdrawalDTO;
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

    public static DepositDTO transferToDepositDTO(@NonNull Transfer transfer) {

        DepositDTO depositDTO = new DepositDTO();
        depositDTO.setId(Long.toString(transfer.getId()));
        depositDTO.setExecutedTimestamp(Long.toString(transfer.getExecutedTimestamp().getTime()));
        depositDTO.setCorrespondentAccountId(Long.toString(transfer.getWithdrawalAccountId()));
        depositDTO.setDepositAmount(AmountMapper.longToString(transfer.getDepositAmount(),
                transfer.getDepositAccountCurrency()));

        return depositDTO;

    }

    public static WithdrawalDTO transferToWithdrawalDTO(@NonNull Transfer transfer) {

        WithdrawalDTO withdrawalDTO = new WithdrawalDTO();
        withdrawalDTO.setId(Long.toString(transfer.getId()));
        withdrawalDTO.setExecutedTimestamp(Long.toString(transfer.getExecutedTimestamp().getTime()));
        withdrawalDTO.setCorrespondentAccountId(Long.toString(transfer.getDepositAccountId()));
        withdrawalDTO.setWithdrawalAmount(AmountMapper.longToString(transfer.getWithdrawalAmount(),
                transfer.getWithdrawalAccountCurrency()));

        return withdrawalDTO;

    }

    public static List<DepositDTO> transferToDepositDTO(@NonNull List<Transfer> transfers) {

        return transfers.stream()
                .map(TransferMapper::transferToDepositDTO)
                .collect(Collectors.toList());

    }

    public static List<WithdrawalDTO> transferToWithdrawalDTO(@NonNull List<Transfer> transfers) {

        return transfers.stream()
                .map(TransferMapper::transferToWithdrawalDTO)
                .collect(Collectors.toList());

    }

    private TransferMapper() {}

}
