package com.revolut.transfer.service.impl;

import com.revolut.transfer.mapper.TransferMapper;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Transfer;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.TransferRepository;
import com.revolut.transfer.service.TransferService;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static java.util.Arrays.asList;

class TransferServiceImpl implements TransferService {

    private final RepositoryManagerFactory repositoryManagerFactory;

    TransferServiceImpl(RepositoryManagerFactory repositoryManagerFactory) {
        this.repositoryManagerFactory = repositoryManagerFactory;
    }

    @Override
    public void transfer(@NonNull final Transfer transfer) {

        if (transfer.getWithdrawalAccountId() == transfer.getDepositAccountId()) {
            return;
        }

        try(final RepositoryManager repositoryManager =
                repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();

            final List<Account> accounts = accountRepository.findAllById(
                    asList(transfer.getWithdrawalAccountId(), transfer.getDepositAccountId()),
                    true);
            TransferMapper.mapAccounts(transfer, accounts);
            validateTransferAccounts(transfer);

            accountRepository.withdraw(transfer.getWithdrawalAccount(), transfer.getWithdrawalAmount());
            validateWithdrawalAmount(transfer.getWithdrawalAccount());
            accountRepository.deposit(transfer.getDepositAccount(), transfer.getDepositAmount());

            // todo: if transaction will be rolled back Account remains with wrong amount

            final TransferRepository transferRepository = repositoryManager.getTransferRepository();
            transferRepository.create(transfer);

            repositoryManager.commit();

        }

    }

    private void validateTransferAccounts(Transfer transfer) {

        if (Objects.isNull(transfer.getWithdrawalAccount())) {
            throw new IllegalStateException(
                    String.format("Unknown withdrawal account '%s'", transfer.getWithdrawalAccountId()));
        }

        if (!transfer.getWithdrawalAccount().isActive()) {
            throw new IllegalStateException(
                    String.format("Withdrawal account '%s' is deactivated", transfer.getWithdrawalAccountId()));
        }

        if (Objects.isNull(transfer.getDepositAccount())) {
            throw new IllegalStateException(
                    String.format("Unknown deposit account '%s'", transfer.getDepositAccountId()));
        }

        if (!transfer.getDepositAccount().isActive()) {
            throw new IllegalStateException(
                    String.format("Deposit account '%s' is deactivated", transfer.getDepositAccountId()));
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

    }

    private void validateWithdrawalAmount(Account withdrawalAccount) {

        if (withdrawalAccount.getBalance() < 0) {
            throw new IllegalStateException(
                    String.format("Not enough money for account '%s' withdrawal",
                            withdrawalAccount.getId()));
        }

    }

}

