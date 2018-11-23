package com.revolut.transfer.model;

public enum Currency {

    RUB(2),
    USD(2),
    BTC(8);

    private final int precision;

    Currency(int precision) {
        this.precision = precision;
    }

    public int getPrecision() {
        return precision;
    }

}
