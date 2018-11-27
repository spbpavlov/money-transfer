package com.revolut.transfer.service.impl;

import com.revolut.transfer.dto.DepositDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.dto.WithdrawalDTO;
import com.revolut.transfer.mapper.TransferMapper;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Transfer;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.TransferRepository;
import com.revolut.transfer.service.TransferService;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
    public TransferDTO transfer(@NonNull final TransferDTO transferDTO) {

        final Transfer transfer = TransferMapper.transferDTOtoTransfer(transferDTO);

        if (transfer.getWithdrawalAccountId() == transfer.getDepositAccountId()) {
            throw new IllegalStateException(
                    String.format("Transfer within same account '%s' is not allowed", transfer.getWithdrawalAccountId()));
        }

        // If any exception occurs transaction will be rolled back automatically on repositoryManager close.
        try(final RepositoryManager repositoryManager =
                repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();

            final List<Long> accountIdList = asList(transfer.getWithdrawalAccountId(), transfer.getDepositAccountId());
            Collections.sort(accountIdList); // sorting prevents possible deadlocks
            final List<Account> accounts = new ArrayList<>();
            for (Long accountId : accountIdList) {
                accounts.add(accountRepository.lockAndGetById(accountId));
            }
            mapAccounts(transfer, accounts);
            validateTransfer(transfer);

            accountRepository.withdraw(transfer.getWithdrawalAccount(), transfer.getWithdrawalAmount());
            accountRepository.deposit(transfer.getDepositAccount(), transfer.getDepositAmount());
            final TransferRepository transferRepository = repositoryManager.getTransferRepository();
            final Transfer executedTransfer = transferRepository.create(transfer);
            final TransferDTO executedTransferDTO = TransferMapper.transferToTransferDTO(executedTransfer);
            repositoryManager.commit();

            return executedTransferDTO;

        }

    }

    @Override
    public List<DepositDTO> getDeposits(long accountId, Timestamp start, Timestamp end) {

        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final Account account = accountRepository.getById(accountId);
            validateAccount(account, accountId,false);

            final TransferRepository transferRepository = repositoryManager.getTransferRepository();
            final List<Transfer> deposits = transferRepository.
                    findDepositByAccountId(accountId, start, end);
            deposits.forEach(transfer -> transfer.setDepositAccountCurrency(account.getCurrency()));

            return TransferMapper.transferToDepositDTO(deposits);
        }

    }

    @Override
    public List<WithdrawalDTO> getWithdrawals(long accountId, Timestamp start, Timestamp end) {

        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final Account account = accountRepository.getById(accountId);
            validateAccount(account, accountId, false);

            final TransferRepository transferRepository = repositoryManager.getTransferRepository();
            final List<Transfer> withdrawals = transferRepository.
                    findWithdrawalByAccountId(accountId,  start, end);
            withdrawals.forEach(transfer -> transfer.setWithdrawalAccountCurrency(account.getCurrency()));
            return TransferMapper.transferToWithdrawalDTO(withdrawals);
        }

    }

    private void mapAccounts(@NonNull Transfer transfer, @NonNull List<Account> accounts) {

        for (Account account : accounts) {
            if (account.getId() ==  transfer.getWithdrawalAccountId()) {
                transfer.setWithdrawalAccount(account);
            } else if (account.getId() ==  transfer.getDepositAccountId()) {
                transfer.setDepositAccount(account);
            }
        }

    }

    private void validateTransfer(Transfer transfer) {

        validateAccount(transfer.getWithdrawalAccount(), transfer.getWithdrawalAccountId(), true);
        validateAccount(transfer.getDepositAccount(), transfer.getDepositAccountId(), true);

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

    private void validateAccount(Account account, long accountId, boolean mustBeActive) {

        if (Objects.isNull(account)) {
            throw new IllegalStateException(
                    String.format("Unknown account '%s'", accountId));
        }

        if (mustBeActive && !account.isActive()) {
            throw new IllegalStateException(
                    String.format("Account '%s' is deactivated", account.getId()));
        }

    }

}

