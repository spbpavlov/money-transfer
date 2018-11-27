package com.revolut.transfer.validator;

import com.revolut.transfer.model.Account;

import java.util.NoSuchElementException;
import java.util.Objects;

final public class AccountValidator {

    public static void validateAccount(Account account, long accountId, boolean mustBeActive) {

        if (Objects.isNull(account)) {
            throw new NoSuchElementException(
                    String.format("Unknown account '%s'", accountId));
        }

        if (mustBeActive && !account.isActive()) {
            throw new IllegalStateException(
                    String.format("Account '%s' is deactivated", account.getId()));
        }

    }

    private AccountValidator() {}

}
