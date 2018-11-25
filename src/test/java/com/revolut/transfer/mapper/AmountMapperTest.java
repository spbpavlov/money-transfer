package com.revolut.transfer.mapper;

import com.revolut.transfer.model.Currency;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;

public class AmountMapperTest {

    @Test
    public void testLongToStringRUB() {

        String result;

        result = AmountMapper.longToString(1234567, Currency.RUB);
        assertEquals("12345.67", result);

        result = AmountMapper.longToString(-1234567, Currency.RUB);
        assertEquals("-12345.67", result);

        result = AmountMapper.longToString(123, Currency.RUB);
        assertEquals("1.23", result);

        result = AmountMapper.longToString(-123, Currency.RUB);
        assertEquals("-1.23", result);

        result = AmountMapper.longToString(12, Currency.RUB);
        assertEquals("0.12", result);

        result = AmountMapper.longToString(-12, Currency.RUB);
        assertEquals("-0.12", result);

        result = AmountMapper.longToString(1, Currency.RUB);
        assertEquals("0.01", result);

        result = AmountMapper.longToString(-1, Currency.RUB);
        assertEquals("-0.01", result);

        result = AmountMapper.longToString(0, Currency.RUB);
        assertEquals("0.00", result);

        result = AmountMapper.longToString(-0, Currency.RUB);
        assertEquals("0.00", result);

    }

    @Test
    public void testStringToLongUSDPrettyFormat() {

        long result;

        result = AmountMapper.stringToLong("12345.67", Currency.USD);
        assertEquals(1234567, result);

        result = AmountMapper.stringToLong("-12345.67", Currency.USD);
        assertEquals(-1234567, result);

        result = AmountMapper.stringToLong("1.23", Currency.USD);
        assertEquals(123, result);

        result = AmountMapper.stringToLong("-1.23", Currency.USD);
        assertEquals(-123, result);

        result = AmountMapper.stringToLong("0.12", Currency.USD);
        assertEquals(12, result);

        result = AmountMapper.stringToLong("-0.12", Currency.USD);
        assertEquals(-12, result);

        result = AmountMapper.stringToLong("0.01", Currency.USD);
        assertEquals(1, result);

        result = AmountMapper.stringToLong("-0.01", Currency.USD);
        assertEquals(-1, result);

        result = AmountMapper.stringToLong("0.00", Currency.USD);
        assertEquals(0, result);

        result = AmountMapper.stringToLong("-0.00", Currency.USD);
        assertEquals(0, result);

    }

    @Test
    public void testStringToLongUSDValidFormatWithDot() {

        long result;

        result = AmountMapper.stringToLong("12345.6", Currency.USD);
        assertEquals(1234560, result);

        result = AmountMapper.stringToLong("-12345.6", Currency.USD);
        assertEquals(-1234560, result);

        result = AmountMapper.stringToLong("1.2", Currency.USD);
        assertEquals(120, result);

        result = AmountMapper.stringToLong("-1.2", Currency.USD);
        assertEquals(-120, result);

        result = AmountMapper.stringToLong("0.1", Currency.USD);
        assertEquals(10, result);

        result = AmountMapper.stringToLong("-0.1", Currency.USD);
        assertEquals(-10, result);

        result = AmountMapper.stringToLong("0.0", Currency.USD);
        assertEquals(0, result);

        result = AmountMapper.stringToLong("-0.0", Currency.USD);
        assertEquals(0, result);

    }

    @Test
    public void testStringToLongUSDValidFormatWithoutDotWithSpaces() {

        long result;

        result = AmountMapper.stringToLong("12345", Currency.USD);
        assertEquals(1234500, result);

        result = AmountMapper.stringToLong("-12345 ", Currency.USD);
        assertEquals(-1234500, result);

        result = AmountMapper.stringToLong(" 1", Currency.USD);
        assertEquals(100, result);

        result = AmountMapper.stringToLong("-1  ", Currency.USD);
        assertEquals(-100, result);

        result = AmountMapper.stringToLong("  0", Currency.USD);
        assertEquals(0, result);

        result = AmountMapper.stringToLong("-0", Currency.USD);
        assertEquals(0, result);

    }

    @Test(expected = InvalidParameterException.class)
    public void testStringToLongUSDEmptyString() {
        AmountMapper.stringToLong("", Currency.USD);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToLongUSDNotValidFormatWithDot() {
        AmountMapper.stringToLong("12345.123", Currency.USD);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToLongUSDNotValidFormatWith2Dot() {
        AmountMapper.stringToLong("12.345.12", Currency.USD);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToLongUSDNotValidFormatWithComma() {
        AmountMapper.stringToLong("12345,12", Currency.USD);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToLongUSDNotValidFormatWithLetter() {
        AmountMapper.stringToLong("A12345.12", Currency.USD);
    }

}