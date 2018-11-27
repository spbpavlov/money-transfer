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
import com.revolut.transfer.validator.AccountValidator;
import com.revolut.transfer.validator.TransferValidator;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            TransferValidator.validateTransfer(transfer);

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
            AccountValidator.validateAccount(account, accountId,false);

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
            AccountValidator.validateAccount(account, accountId, false);

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

}

