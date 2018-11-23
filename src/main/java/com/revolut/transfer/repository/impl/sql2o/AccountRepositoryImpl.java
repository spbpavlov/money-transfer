package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.AccountRepository;
import org.sql2o.Connection;

import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {

    private final Connection con;

    AccountRepositoryImpl(Connection con) {
        this.con = con;
    }

    @Override
    public Iterable<Account> findAllByCustomerId(long customerId) {
        return null;
    }

    @Override
    public Account changeBalance(Account account, long newBalance) {
        return null;
    }

    @Override
    public <S extends Account> S create(S var1) {
        return null;
    }

    @Override
    public Optional<Account> findById(Long var1) {
        return Optional.empty();
    }

    @Override
    public Iterable<Account> findAllById(Iterable<Long> var1) {
        return null;
    }

    @Override
    public void deleteById(Long var1) {

    }


}
