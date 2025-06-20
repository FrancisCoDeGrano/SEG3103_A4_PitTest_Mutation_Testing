package com.banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Test Case 2 - Advanced Account Features (Member 2)
 *
 * This test class covers advanced account functionality including:
 * - Interest calculations for different account types
 * - Transfer operations between accounts
 * - Daily withdrawal limits and boundary testing
 * - Account type-specific behavior
 * - Edge cases and boundary conditions
 *
 * Expected Mutation Coverage: ~70-80%
 * This test adds comprehensive edge case testing and boundary value analysis.
 */
@DisplayName("Advanced Account Features Tests")
public class AdvancedAccountFeaturesTest {

    private Account checkingAccount;
    private Account savingsAccount;
    private Account premiumAccount;

    @BeforeEach
    void setUp() {
        checkingAccount = new Account("CHK002", AccountType.CHECKING, new BigDecimal("1000.00"));
        savingsAccount = new Account("SAV002", AccountType.SAVINGS, new BigDecimal("5000.00"));
        premiumAccount = new Account("PRM002", AccountType.PREMIUM, new BigDecimal("10000.00"));
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    @DisplayName("Interest Calculation - All Account Types")
    void testInterestCalculation_AllAccountTypes(AccountType accountType) {
        Account account = new Account("TEST002", accountType, new BigDecimal("1000.00"));
        BigDecimal interest = account.calculateInterest();

        switch (accountType) {
            case CHECKING:
                assertEquals(new BigDecimal("5.00"), interest); // 0.5% of 1000
                break;
            case SAVINGS:
                assertEquals(new BigDecimal("20.00"), interest); // 2% of 1000
                break;
            case PREMIUM:
                assertEquals(new BigDecimal("35.00"), interest); // 3.5% of 1000
                break;
        }
    }

    @Test
    @DisplayName("Interest Calculation - Zero Balance")
    void testInterestCalculation_ZeroBalance() {
        Account zeroAccount = new Account("ZERO002", AccountType.SAVINGS, BigDecimal.ZERO);
        BigDecimal interest = zeroAccount.calculateInterest();

        assertEquals(BigDecimal.ZERO, interest);
    }

    @Test
    @DisplayName("Interest Calculation - Inactive Account")
    void testInterestCalculation_InactiveAccount() {
        Account account = new Account("INACTIVE002", AccountType.SAVINGS, BigDecimal.ZERO);
        account.closeAccount();

        BigDecimal interest = account.calculateInterest();
        assertEquals(BigDecimal.ZERO, interest);
    }

    @Test
    @DisplayName("Apply Interest - Active Account")
    void testApplyInterest_ActiveAccount() {
        BigDecimal initialBalance = savingsAccount.getBalance();
        BigDecimal expectedInterest = savingsAccount.calculateInterest();

        savingsAccount.applyInterest();

        assertEquals(initialBalance.add(expectedInterest), savingsAccount.getBalance());
        assertEquals(1, savingsAccount.getTransactionHistory().size());
        assertEquals(TransactionType.INTEREST,
                savingsAccount.getTransactionHistory().get(0).getType());
    }

    @Test
    @DisplayName("Transfer - Successful Transfer")
    void testTransfer_SuccessfulTransfer() {
        BigDecimal transferAmount = new BigDecimal("300.00");
        BigDecimal senderInitialBalance = checkingAccount.getBalance();
        BigDecimal receiverInitialBalance = savingsAccount.getBalance();

        boolean result = checkingAccount.transfer(savingsAccount, transferAmount, "Test transfer");

        assertTrue(result);
        assertEquals(senderInitialBalance.subtract(transferAmount), checkingAccount.getBalance());
        assertEquals(receiverInitialBalance.add(transferAmount), savingsAccount.getBalance());

        // Both accounts should have transaction records
        assertEquals(1, checkingAccount.getTransactionHistory().size());
        assertEquals(1, savingsAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Transfer - Insufficient Funds")
    void testTransfer_InsufficientFunds() {
        BigDecimal transferAmount = new BigDecimal("2000.00"); // More than sender balance
        BigDecimal senderInitialBalance = checkingAccount.getBalance();
        BigDecimal receiverInitialBalance = savingsAccount.getBalance();

        boolean result = checkingAccount.transfer(savingsAccount, transferAmount, "Test transfer");

        assertFalse(result);
        assertEquals(senderInitialBalance, checkingAccount.getBalance());
        assertEquals(receiverInitialBalance, savingsAccount.getBalance());
        assertEquals(0, checkingAccount.getTransactionHistory().size());
        assertEquals(0, savingsAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Transfer - Null Target Account")
    void testTransfer_NullTargetAccount() {
        BigDecimal transferAmount = new BigDecimal("100.00");
        BigDecimal senderInitialBalance = checkingAccount.getBalance();

        boolean result = checkingAccount.transfer(null, transferAmount, "Test transfer");

        assertFalse(result);
        assertEquals(senderInitialBalance, checkingAccount.getBalance());
        assertEquals(0, checkingAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Transfer - Inactive Target Account")
    void testTransfer_InactiveTargetAccount() {
        Account inactiveAccount = new Account("INACTIVE003", AccountType.CHECKING, BigDecimal.ZERO);
        inactiveAccount.closeAccount();

        BigDecimal transferAmount = new BigDecimal("100.00");
        BigDecimal senderInitialBalance = checkingAccount.getBalance();

        boolean result = checkingAccount.transfer(inactiveAccount, transferAmount, "Test transfer");

        assertFalse(result);
        assertEquals(senderInitialBalance, checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Daily Withdrawal Limit - Regular Account")
    void testDailyWithdrawalLimit_RegularAccount() {
        // Regular accounts have $1000 daily limit
        assertEquals(new BigDecimal("1000.00"), checkingAccount.getDailyWithdrawalLimit());

        // First withdrawal within limit should succeed
        assertTrue(checkingAccount.withdraw(new BigDecimal("800.00"), "Withdrawal 1"));
        assertEquals(new BigDecimal("800.00"), checkingAccount.getTodayWithdrawn());

        // Second withdrawal that would exceed limit should fail
        assertFalse(checkingAccount.withdraw(new BigDecimal("300.00"), "Withdrawal 2"));
        assertEquals(new BigDecimal("800.00"), checkingAccount.getTodayWithdrawn());

        // Withdrawal within remaining limit should succeed
        assertTrue(checkingAccount.withdraw(new BigDecimal("200.00"), "Withdrawal 3"));
        assertEquals(new BigDecimal("1000.00"), checkingAccount.getTodayWithdrawn());
    }

    @Test
    @DisplayName("Daily Withdrawal Limit - Premium Account")
    void testDailyWithdrawalLimit_PremiumAccount() {
        // Premium accounts have $5000 daily limit
        assertEquals(new BigDecimal("5000.00"), premiumAccount.getDailyWithdrawalLimit());

        // Large withdrawal within premium limit should succeed
        assertTrue(premiumAccount.withdraw(new BigDecimal("4000.00"), "Large withdrawal"));
        assertEquals(new BigDecimal("4000.00"), premiumAccount.getTodayWithdrawn());

        // Withdrawal that would exceed premium limit should fail
        assertFalse(premiumAccount.withdraw(new BigDecimal("1500.00"), "Excessive withdrawal"));
        assertEquals(new BigDecimal("4000.00"), premiumAccount.getTodayWithdrawn());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "999.99", "1000.00"})
    @DisplayName("Boundary Value Testing - Withdrawal Amounts")
    void testBoundaryValues_WithdrawalAmounts(String amountStr) {
        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal initialBalance = checkingAccount.getBalance();

        boolean result = checkingAccount.withdraw(amount, "Boundary test");

        assertTrue(result);
        assertEquals(initialBalance.subtract(amount), checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Precision Testing - Decimal Calculations")
    void testPrecisionTesting_DecimalCalculations() {
        Account precisionAccount = new Account("PREC002", AccountType.CHECKING, new BigDecimal("100.00"));

        // Test with amounts that might cause precision issues
        assertTrue(precisionAccount.deposit(new BigDecimal("33.33"), "Precision test 1"));
        assertTrue(precisionAccount.deposit(new BigDecimal("33.33"), "Precision test 2"));
        assertTrue(precisionAccount.deposit(new BigDecimal("33.34"), "Precision test 3"));

        // Balance should be exactly 200.00
        assertEquals(new BigDecimal("200.00"), precisionAccount.getBalance());
    }

    @Test
    @DisplayName("Transfer Rollback - Failed Deposit")
    void testTransferRollback_FailedDeposit() {
        // Create an inactive target account to simulate failed deposit
        Account inactiveTarget = new Account("INACTIVE004", AccountType.CHECKING, BigDecimal.ZERO);
        inactiveTarget.closeAccount();

        BigDecimal transferAmount = new BigDecimal("100.00");
        BigDecimal senderInitialBalance = checkingAccount.getBalance();

        boolean result = checkingAccount.transfer(inactiveTarget, transferAmount, "Test rollback");

        assertFalse(result);
        assertEquals(senderInitialBalance, checkingAccount.getBalance());
        // Should have no net transactions (withdrawal + rollback deposit = 0 net transactions)
        assertEquals(0, checkingAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Account Closure - Non-Zero Balance")
    void testAccountClosure_NonZeroBalance() {
        assertTrue(checkingAccount.isActive());
        assertTrue(checkingAccount.getBalance().compareTo(BigDecimal.ZERO) > 0);

        checkingAccount.closeAccount();

        // Account should remain active if balance is not zero
        assertTrue(checkingAccount.isActive());
        assertEquals(0, checkingAccount.getTransactionHistory().size());
    }
}