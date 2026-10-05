package org.example.spacecompany;

import java.math.BigDecimal;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.exception.InsufficientFundsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceServiceTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = TestFixtures.newWorld();
    }

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    @Test
    void depositIncreasesBalanceAndRecordsIncome() {
        BigDecimal before = world.finance.getBalance();
        world.finance.deposit(new BigDecimal("1500.50"), "Test income");

        assertEquals(0, new BigDecimal("101500.50").compareTo(world.finance.getBalance()));
        assertTrue(before.compareTo(world.finance.getBalance()) < 0);
        assertEquals(0, new BigDecimal("1500.50").compareTo(world.finance.totalIncome()));
    }

    @Test
    void withdrawDecreasesBalanceAndRecordsExpense() {
        world.finance.withdraw(new BigDecimal("2500"), "Test expense");

        assertEquals(0, new BigDecimal("97500").compareTo(world.finance.getBalance()));
        assertEquals(0, new BigDecimal("2500").compareTo(world.finance.totalExpenses()));
    }

    @Test
    void withdrawBeyondBalanceThrows() {
        assertThrows(InsufficientFundsException.class,
                () -> world.finance.withdraw(new BigDecimal("999999999"), "Too much"));
    }

    @Test
    void netProfitIsIncomeMinusExpenses() {
        world.finance.deposit(new BigDecimal("5000"), "In");
        world.finance.withdraw(new BigDecimal("2000"), "Out");

        assertEquals(0, new BigDecimal("3000").compareTo(world.finance.netProfit()));
    }

    @Test
    void moneyUsesExactDecimalArithmetic() {
        // 0.1 + 0.2 must be exactly 0.3 — the reason we use BigDecimal, not double.
        world.finance.deposit(new BigDecimal("0.10"), "dime");
        world.finance.deposit(new BigDecimal("0.20"), "two dimes");
        assertEquals(0, new BigDecimal("0.30").compareTo(world.finance.totalIncome()));
    }
}
