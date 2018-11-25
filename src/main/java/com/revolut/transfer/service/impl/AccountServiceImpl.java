package com.revolut.transfer.service.impl;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.AccountRepository;
import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.service.AccountService;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

class AccountServiceImpl implements AccountService {

    private final RepositoryManagerFactory repositoryManagerFactory;

    AccountServiceImpl(RepositoryManagerFactory repositoryManagerFactory) {
        this.repositoryManagerFactory = repositoryManagerFactory;
    }

    @Override
    public List<Account> findAllByCustomerId(long customerId) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            return accountRepository.findAllByCustomerId(customerId, false);
        }
    }

    @Override
    public Account create(@NonNull Account account) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            account.setActive(true);
            account = accountRepository.create(account);
            repositoryManager.commit();
            return account;
        }
    }

    @Override
    public Account findById(Long id) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {
            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            return accountRepository.findById(id, false);

            //todo ? read only transaction
        }
    }

    @Override
    public Account deactivate(Long id) {
        try (final RepositoryManager repositoryManager =
                     repositoryManagerFactory.getRepositoryManager(TRANSACTION_READ_COMMITTED)) {

            final AccountRepository accountRepository = repositoryManager.getAccountRepository();
            Account account = accountRepository.findById(id, true);
            validateAccount(account, id);

            if (account.isActive()) {

                if (account.getBalance() > 0) {
                    throw new IllegalStateException(
                            String.format("Account '%s' balance must be empty for deactivation", account.getId()));
                }

                account = accountRepository.deactivate(account);
                repositoryManager.commit();

            }

            return account;

        }
    }

    private void validateAccount(Account account, long id) {

        if (Objects.isNull(account)) {
            throw new IllegalStateException(
                    String.format("Unknown account '%s'", id));
        }

    }

}
