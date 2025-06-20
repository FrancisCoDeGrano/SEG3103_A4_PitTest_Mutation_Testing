package com.banking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculator {

    public static BigDecimal calculateCompoundInterest(BigDecimal principal,
                                                       BigDecimal rate,
                                                       int timeYears,
                                                       int compoundFrequency) {
        if (principal == null || rate == null || timeYears < 0 || compoundFrequency <= 0) {
            throw new IllegalArgumentException("Invalid parameters for compound interest calculation");
        }

        if (principal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Formula: A = P(1 + r/n)^(nt)
        BigDecimal ratePerPeriod = rate.divide(new BigDecimal(compoundFrequency), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(ratePerPeriod);

        int totalPeriods = timeYears * compoundFrequency;
        BigDecimal result = principal;

        // Calculate (1 + r/n)^(nt) using repeated multiplication for precision
        for (int i = 0; i < totalPeriods; i++) {
            result = result.multiply(onePlusRate);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateLoanPayment(BigDecimal principal,
                                                  BigDecimal monthlyRate,
                                                  int months) {
        if (principal == null || monthlyRate == null || months <= 0) {
            throw new IllegalArgumentException("Invalid loan parameters");
        }

        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        }

        // Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal numerator = monthlyRate;
        BigDecimal denominator = BigDecimal.ONE;

        // Calculate (1+r)^n
        for (int i = 0; i < months; i++) {
            numerator = numerator.multiply(onePlusRate);
            denominator = denominator.multiply(onePlusRate);
        }

        denominator = denominator.subtract(BigDecimal.ONE);
        BigDecimal payment = principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);

        return payment;
    }

    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number <= 3) return true;
        if (number % 2 == 0 || number % 3 == 0) return false;

        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}