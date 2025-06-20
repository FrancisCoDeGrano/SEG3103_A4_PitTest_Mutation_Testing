// File: src/test/java/com/banking/BasicAccountOperationsTest.java
package com.banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Test Case 1 - Basic Account Operations (Member 1)
 *
 * This test class covers fundamental account operations including:
 * - Account creation with validation
 * - Basic deposit and withdrawal operations
 * - Balance inquiries
 * - Account status management
 *
 * Expected Mutation Coverage: ~40-50%
 * This basic test will catch obvious mutations but miss edge cases and boundary conditions.
 */
@DisplayName("Basic Account Operations Tests")
public class BasicAccountOperationsTest {

    private Account checkingAccount;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        checkingAccount = new Account("CHK001", AccountType.CHECKING, new BigDecimal("1000.00"));
        savingsAccount = new Account("SAV001", AccountType.SAVINGS, new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Account Creation - Valid Parameters")
    void testAccountCreation_ValidParameters() {
        Account account = new Account("TEST001", AccountType.CHECKING, new BigDecimal("100.00"));

        assertEquals("TEST001", account.getAccountNumber());
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertTrue(account.isActive());
        assertNotNull(account.getTransactionHistory());
    }

    @Test
    @DisplayName("Account Creation - Invalid Parameters")
    void testAccountCreation_InvalidParameters() {
        // Test null account number
        assertThrows(IllegalArgumentException.class, () ->
                new Account(null, AccountType.CHECKING, new BigDecimal("100.00")));

        // Test empty account number
        assertThrows(IllegalArgumentException.class, () ->
                new Account("", AccountType.CHECKING, new BigDecimal("100.00")));

        // Test null account type
        assertThrows(IllegalArgumentException.class, () ->
                new Account("TEST001", null, new BigDecimal("100.00")));

        // Test negative initial balance
        assertThrows(IllegalArgumentException.class, () ->
                new Account("TEST001", AccountType.CHECKING, new BigDecimal("-100.00")));
    }

    @Test
    @DisplayName("Deposit - Valid Amount")
    void testDeposit_ValidAmount() {
        BigDecimal initialBalance = checkingAccount.getBalance();
        BigDecimal depositAmount = new BigDecimal("250.00");

        boolean result = checkingAccount.deposit(depositAmount, "Test deposit");

        assertTrue(result);
        assertEquals(initialBalance.add(depositAmount), checkingAccount.getBalance());
        assertEquals(1, checkingAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Deposit - Invalid Amount")
    void testDeposit_InvalidAmount() {
        BigDecimal initialBalance = checkingAccount.getBalance();

        // Test null amount
        boolean result1 = checkingAccount.deposit(null, "Test deposit");
        assertFalse(result1);
        assertEquals(initialBalance, checkingAccount.getBalance());

        // Test zero amount
        boolean result2 = checkingAccount.deposit(BigDecimal.ZERO, "Test deposit");
        assertFalse(result2);
        assertEquals(initialBalance, checkingAccount.getBalance());

        // Test negative amount
        boolean result3 = checkingAccount.deposit(new BigDecimal("-50.00"), "Test deposit");
        assertFalse(result3);
        assertEquals(initialBalance, checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Withdrawal - Valid Amount")
    void testWithdrawal_ValidAmount() {
        BigDecimal initialBalance = checkingAccount.getBalance();
        BigDecimal withdrawalAmount = new BigDecimal("200.00");

        boolean result = checkingAccount.withdraw(withdrawalAmount, "Test withdrawal");

        assertTrue(result);
        assertEquals(initialBalance.subtract(withdrawalAmount), checkingAccount.getBalance());
        assertEquals(1, checkingAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Withdrawal - Insufficient Funds")
    void testWithdrawal_InsufficientFunds() {
        BigDecimal initialBalance = checkingAccount.getBalance();
        BigDecimal withdrawalAmount = new BigDecimal("2000.00"); // More than balance

        boolean result = checkingAccount.withdraw(withdrawalAmount, "Test withdrawal");

        assertFalse(result);
        assertEquals(initialBalance, checkingAccount.getBalance()); // Balance unchanged
        assertEquals(0, checkingAccount.getTransactionHistory().size()); // No transaction recorded
    }

    @Test
    @DisplayName("Account Status - Active Account Operations")
    void testActiveAccountOperations() {
        assertTrue(checkingAccount.isActive());

        // Active account should allow deposits and withdrawals
        assertTrue(checkingAccount.deposit(new BigDecimal("100.00"), "Test"));
        assertTrue(checkingAccount.withdraw(new BigDecimal("50.00"), "Test"));
    }

    @Test
    @DisplayName("Account Closure - Zero Balance")
    void testAccountClosure_ZeroBalance() {
        // Create account with zero balance
        Account zeroAccount = new Account("ZERO001", AccountType.CHECKING, BigDecimal.ZERO);

        zeroAccount.closeAccount();

        assertFalse(zeroAccount.isActive());
        assertEquals(1, zeroAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Multiple Operations - Balance Tracking")
    void testMultipleOperations_BalanceTracking() {
        BigDecimal initialBalance = checkingAccount.getBalance(); // 1000.00

        // Perform multiple operations
        checkingAccount.deposit(new BigDecimal("500.00"), "Deposit 1");
        checkingAccount.withdraw(new BigDecimal("200.00"), "Withdrawal 1");
        checkingAccount.deposit(new BigDecimal("100.00"), "Deposit 2");

        BigDecimal expectedBalance = initialBalance
                .add(new BigDecimal("500.00"))
                .subtract(new BigDecimal("200.00"))
                .add(new BigDecimal("100.00"));

        assertEquals(expectedBalance, checkingAccount.getBalance());
        assertEquals(3, checkingAccount.getTransactionHistory().size());
    }
}
