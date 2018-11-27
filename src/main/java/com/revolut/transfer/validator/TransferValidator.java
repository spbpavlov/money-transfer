package com.revolut.transfer.validator;

import com.revolut.transfer.model.Transfer;

final public class TransferValidator {

    public static void validateTransfer(Transfer transfer) {

        AccountValidator.validateAccount(transfer.getWithdrawalAccount(), transfer.getWithdrawalAccountId(), true);
        AccountValidator.validateAccount(transfer.getDepositAccount(), transfer.getDepositAccountId(), true);

        if (transfer.getWithdrawalAccountId() == transfer.getDepositAccountId()) {
            throw new IllegalStateException(
                    String.format("Transfer within same account '%s' is not allowed", transfer.getWithdrawalAccountId()));
        }

        if (!transfer.getWithdrawalAccount().getCurrency().equals(transfer.getWithdrawalAccountCurrency())) {
            throw new IllegalStateException(
                    String.format("Withdrawal currency '%s' does not match to withdrawal account currency '%s'",
                            transfer.getWithdrawalAccountCurrency(),
                            transfer.getWithdrawalAccount().getCurrency()));
        }

        if (!transfer.getDepositAccount().getCurrency().equals(transfer.getDepositAccountCurrency())) {
            throw new IllegalStateException(
                    String.format("Deposit currency '%s' does not match to deposit account currency '%s'",
                            transfer.getDepositAccountCurrency(),
                            transfer.getDepositAccount().getCurrency()));
        }

        if (transfer.getWithdrawalAccount().getBalance() - transfer.getWithdrawalAmount() < 0) {
            throw new IllegalStateException(
                    String.format("Not enough money for account '%s' withdrawal",
                            transfer.getWithdrawalAccount().getId()));
        }

    }

    private TransferValidator() {}
}
