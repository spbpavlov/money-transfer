package com.revolut.transfer.service.impl;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.mapper.AccountMapper;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.service.AccountService;
import com.revolut.transfer.validator.AccountValidator;
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
            final List<Account> accounts = accountRepository.findAllByCustomerId(customerId);
            return AccountMapper.accountToAccountDTO(accounts);
        }
    }

    @Override
    public AccountDTO create(@NonNull AccountDTO accountDTO) {

        final Account account = AccountMapper.accountDTOToAccount(accountDTO);

        // If any exception occurs transaction will be rolled back automatically on repositoryManager close.
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            account.setActive(true);
            final Account createdAccount = accountRepository.create(account);
            final AccountDTO createdAccountDTO = AccountMapper.accountToAccountDTO(createdAccount);
            repositoryManager.commit();

            return createdAccountDTO;
        }
    }

    @Override
    public AccountDTO getById(long accountId) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final Account account = accountRepository.getById(accountId);

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
        // If any exception occurs transaction will be rolled back automatically on repositoryManager close.
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            final Account account = accountRepository.lockAndGetById(accountId);
            AccountValidator.validateAccount(account, accountId, true);

            if (account.getBalance() > 0) {
                throw new IllegalStateException(
                        String.format("Account '%s' balance must be empty for deactivation", account.getId()));
            }

            final Account deactivatedAccount = accountRepository.deactivate(account);
            final AccountDTO deactivatedAccountDTO = AccountMapper.accountToAccountDTO(deactivatedAccount);
            repositoryManager.commit();

            return deactivatedAccountDTO;
        }
    }

}
