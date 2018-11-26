package com.revolut.transfer.mapper;

import com.revolut.transfer.model.Currency;
import lombok.NonNull;

import java.security.InvalidParameterException;

/**
 * Class for amount conversion in different currencies
 * long <-> String.
 */
final public class AmountMapper {

    private final static String delimiter = ".";

    public static String longToString(long amount, @NonNull Currency currency) {

        final StringBuilder sb = new StringBuilder();
        sb.append(amount < 0 ? -amount : amount);
        final int currencyPrecision = currency.getPrecision();
        while (sb.length() < currencyPrecision + 1) {
            sb.insert(0, '0');
        }
        sb.insert(sb.length() - currencyPrecision, '.');
        if (amount < 0) {
            sb.insert(0, '-');
        }
        return sb.toString();

    }

    public static long stringToLong(@NonNull String amount, @NonNull Currency currency) {

        final StringBuilder sb = new StringBuilder(amount.trim());

        if (amount.isEmpty()) {
            throw new InvalidParameterException(
                    "Amount should not be empty");
        }

        int zeros = currency.getPrecision();
        final int i = sb.indexOf(delimiter);

        if (i > -1) {
            zeros = zeros - (sb.length() - 1 - i);
            sb.deleteCharAt(i);
            final int k = sb.indexOf(delimiter);
            if (k > -1) {
                throw new NumberFormatException(
                        String.format("Amount '%s' should contain only one delimiter", amount));
            }
            if (zeros < 0) {
                throw new NumberFormatException(
                        String.format("Amount '%s' has incorrect precision", amount));
            }
        }

        while (zeros > 0) {
            sb.append('0');
            zeros--;
        }

        return Long.parseLong(sb.toString());

    }

    private AmountMapper() {}

}