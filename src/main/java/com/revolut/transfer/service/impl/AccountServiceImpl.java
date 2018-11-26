package com.revolut.transfer.service.impl;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.mapper.AccountMapper;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.service.AccountService;
import lombok.NonNull;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

class AccountServiceImpl implements AccountService {

    private final RepositoryManagerFactory repositoryManagerFactory;

    AccountServiceImpl(RepositoryManagerFactory repositoryManagerFactory) {
        this.repositoryManagerFactory = repositoryManagerFactory;
    }

    @Override
    public List<AccountDTO> findAllByCustomerId(long customerId) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final List<Account> accounts = accountRepository.findAllByCustomerId(customerId, false);
            return AccountMapper.accountToAccountDTO(accounts);
        }
    }

    @Override
    public AccountDTO create(@NonNull AccountDTO accountDTO) {

        Account account = AccountMapper.accountDTOToAccount(accountDTO);

        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            account.setActive(true);
            account = accountRepository.create(account);
            repositoryManager.commit();
            return AccountMapper.accountToAccountDTO(account);
        }
    }

    @Override
    public AccountDTO findById(long accountId) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final Account account = accountRepository.findById(accountId, false);

            if (Objects.isNull(account)) {
                throw new NoSuchElementException(
                        String.format("Account with id '%s' not found", accountId)
                );
            }

            return AccountMapper.accountToAccountDTO(account);

        }
    }



    @Override
    public AccountDTO deactivate(long accountId) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            Account account = accountRepository.findById(accountId, true);
            validateAccount(account, accountId);

            if (!account.isActive()) {
                throw new IllegalStateException(
                        String.format("Account '%s' already been deactivated", account.getId()));
            }

            if (account.getBalance() > 0) {
                throw new IllegalStateException(
                        String.format("Account '%s' balance must be empty for deactivation", account.getId()));
            }

            account = accountRepository.deactivate(account);
            repositoryManager.commit();

            return AccountMapper.accountToAccountDTO(account);

        }
    }

    private void validateAccount(Account account, long accountId) {

        if (Objects.isNull(account)) {
            throw new IllegalStateException(
                    String.format("Unknown account '%s'", accountId));
        }

    }

}
