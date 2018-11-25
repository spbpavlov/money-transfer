package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.repository.AccountRepository;
import lombok.NonNull;
import org.sql2o.Connection;

import java.util.List;

class AccountRepositoryImpl implements AccountRepository {

    private final Connection con;

    AccountRepositoryImpl(Connection con) {
        this.con = con;
    }

    @Override
    public List<Account> findAllByCustomerId(long customerId, boolean forUpdate) {

        final String sql =
                "SELECT id, customerId, currency, balance, active " +
                        "FROM account " +
                        "WHERE customerId = :customerId " +
                        (forUpdate ? "FOR UPDATE " : "");

        return con.createQuery(sql)
                .addParameter("customerId", customerId)
                .executeAndFetch(Account.class);

    }

    @Override
    public Account deposit(@NonNull Account account, long deposit) {
        final long balance = account.getBalance() + deposit;
        updateBalance(account.getId(), balance);
        return account.withBalance(balance);
    }

    @Override
    public Account withdraw(@NonNull Account account, long withdraw) {
        final long balance = account.getBalance() - withdraw;
        updateBalance(account.getId(), balance);
        return account.withBalance(balance);
    }

    @Override
    public Account create(@NonNull Account account) {

        final String sql = "INSERT INTO account (customerId, currency, balance, active) " +
                "VALUES (:customerId, :currency, :balance, :active)";

        long id = (Long) con.createQuery(sql, true)
                .addParameter("customerId", account.getCustomerId())
                .addParameter("currency", account.getCurrency())
                .addParameter("balance", account.getBalance())
                .addParameter("active", account.isActive())
                .executeUpdate()
                .getKey();

        account.setId(id);

        return account;

    }

    @Override
    public Account findById(long var1, boolean forUpdate) {

        final String sql =
                "SELECT id, customerId, currency, balance, active " +
                        "FROM account " +
                        "WHERE id = :id " +
                        (forUpdate ? "FOR UPDATE " : "");

        final List<Account> accountList = con.createQuery(sql)
                .addParameter("id", var1)
                .executeAndFetch(Account.class);

        if (accountList.isEmpty()) {
            return null;
        } else {
            return accountList.get(0);
        }

    }

    @Override
    public List<Account> findAllById(Iterable<Long> var1, boolean forUpdate) {

        final String sql =
                "SELECT id, customerId, currency, balance, active " +
                        "FROM account " +
                        "WHERE id IN (:id) " +
                        (forUpdate ? "FOR UPDATE " : "");

        return con.createQuery(sql)
                .addParameter("id", var1)
                .executeAndFetch(Account.class);

    }

    @Override
    public Account deactivate(@NonNull Account account) {

        updateActive(account.getId(), false);
        return account.withActive(false);

    }

    private void updateBalance(long id, long balance) {

        final String sql = "UPDATE account " +
                "SET balance = :balance " +
                "WHERE id = :id";

        con.createQuery(sql)
                .addParameter("id", id)
                .addParameter("balance", balance)
                .executeUpdate();

    }

    private void updateActive(long id, boolean active) {

        final String sql = "UPDATE account " +
                "SET active = :active " +
                "WHERE id = :id";

        con.createQuery(sql)
                .addParameter("id", id)
                .addParameter("active", active)
                .executeUpdate();

    }

}
