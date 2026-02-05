package com.banking.core.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testMoneyCreation() {
        Money money = new Money(100.50, "USD");
        assertEquals(100.50, money.getAmount().doubleValue());
        assertEquals("USD", money.getCurrency());
    }

    @Test
    void testMoneyAddition() {
        Money money1 = new Money(100.0, "USD");
        Money money2 = new Money(50.0, "USD");
        Money result = money1.add(money2);
        assertEquals(150.0, result.getAmount().doubleValue());
    }

    @Test
    void testMoneySubtraction() {
        Money money1 = new Money(100.0, "USD");
        Money money2 = new Money(30.0, "USD");
        Money result = money1.subtract(money2);
        assertEquals(70.0, result.getAmount().doubleValue());
    }

    @Test
    void testDifferentCurrencyException() {
        Money usd = new Money(100.0, "USD");
        Money eur = new Money(50.0, "EUR");
        
        assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
        assertThrows(IllegalArgumentException.class, () -> usd.subtract(eur));
    }
}

