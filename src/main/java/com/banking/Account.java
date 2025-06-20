package com.banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private BigDecimal balance;
    private AccountType type;
    private boolean isActive;
    private List<Transaction> transactionHistory;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal todayWithdrawn;
    private LocalDateTime lastTransactionDate;

    public Account(String accountNumber, AccountType type, BigDecimal initialBalance) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = initialBalance.setScale(2, RoundingMode.HALF_UP);
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
        this.dailyWithdrawalLimit = type == AccountType.PREMIUM ?
                new BigDecimal("5000.00") : new BigDecimal("1000.00");
        this.todayWithdrawn = BigDecimal.ZERO;
        this.lastTransactionDate = LocalDateTime.now();
    }

    public boolean deposit(BigDecimal amount, String description) {
        if (!isActive) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        balance = balance.add(amount);
        addTransaction(TransactionType.DEPOSIT, amount, description);
        return true;
    }

    public boolean withdraw(BigDecimal amount, String description) {
        if (!isActive) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (balance.compareTo(amount) < 0) {
            return false; // Insufficient funds
        }

        // Check daily withdrawal limit
        resetDailyLimitIfNeeded();
        if (todayWithdrawn.add(amount).compareTo(dailyWithdrawalLimit) > 0) {
            return false; // Exceeds daily limit
        }

        balance = balance.subtract(amount);
        todayWithdrawn = todayWithdrawn.add(amount);
        addTransaction(TransactionType.WITHDRAWAL, amount, description);
        return true;
    }

    public boolean transfer(Account targetAccount, BigDecimal amount, String description) {
        if (targetAccount == null || !targetAccount.isActive) {
            return false;
        }
        if (!this.withdraw(amount, "Transfer to " + targetAccount.getAccountNumber())) {
            return false;
        }
        if (!targetAccount.deposit(amount, "Transfer from " + this.accountNumber)) {
            // Rollback the withdrawal
            this.deposit(amount, "Rollback failed transfer");
            return false;
        }
        return true;
    }

    public BigDecimal calculateInterest() {
        if (!isActive || balance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal interestRate;
        switch (type) {
            case SAVINGS:
                interestRate = new BigDecimal("0.02"); // 2%
                break;
            case PREMIUM:
                interestRate = new BigDecimal("0.035"); // 3.5%
                break;
            case CHECKING:
            default:
                interestRate = new BigDecimal("0.005"); // 0.5%
                break;
        }

        BigDecimal interest = balance.multiply(interestRate);
        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    public void applyInterest() {
        if (isActive) {
            BigDecimal interest = calculateInterest();
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                balance = balance.add(interest);
                addTransaction(TransactionType.INTEREST, interest, "Monthly interest");
            }
        }
    }

    public void closeAccount() {
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            this.isActive = false;
            addTransaction(TransactionType.ACCOUNT_CLOSURE, BigDecimal.ZERO, "Account closed");
        }
    }

    private void resetDailyLimitIfNeeded() {
        LocalDateTime now = LocalDateTime.now();
        if (lastTransactionDate.toLocalDate().isBefore(now.toLocalDate())) {
            todayWithdrawn = BigDecimal.ZERO;
        }
    }

    private void addTransaction(TransactionType type, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(type, amount, description, LocalDateTime.now());
        transactionHistory.add(transaction);
        lastTransactionDate = LocalDateTime.now();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public AccountType getType() { return type; }
    public boolean isActive() { return isActive; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public BigDecimal getDailyWithdrawalLimit() { return dailyWithdrawalLimit; }
    public BigDecimal getTodayWithdrawn() { return todayWithdrawn; }

}