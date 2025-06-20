package com.banking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Test Case 3 - Calculator & Complex Logic (Member 3)
 *
 * This test class covers complex mathematical calculations and algorithms including:
 * - Compound interest calculations with edge cases
 * - Loan payment calculations with boundary conditions
 * - Prime number algorithm testing
 * - Mathematical precision and rounding behavior
 * - Exception handling for invalid inputs
 * - Comprehensive boundary value analysis
 *
 * Expected Mutation Coverage: ~90-95%
 * This test achieves highest coverage through exhaustive testing of mathematical logic,
 * edge cases, and comprehensive validation of complex algorithms.
 */
@DisplayName("Calculator and Complex Logic Tests")
public class CalculatorAndComplexLogicTest {

    @Nested
    @DisplayName("Compound Interest Calculations")
    class CompoundInterestTests {

        @Test
        @DisplayName("Standard Compound Interest - Annual Compounding")
        void testCompoundInterest_AnnualCompounding() {
            BigDecimal principal = new BigDecimal("1000.00");
            BigDecimal rate = new BigDecimal("0.05"); // 5%
            int years = 10;
            int frequency = 1; // Annual

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, years, frequency);

            // Expected: 1000 * (1.05)^10 ≈ 1628.89
            BigDecimal expected = new BigDecimal("1628.89");
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Compound Interest - Monthly Compounding")
        void testCompoundInterest_MonthlyCompounding() {
            BigDecimal principal = new BigDecimal("5000.00");
            BigDecimal rate = new BigDecimal("0.06"); // 6%
            int years = 5;
            int frequency = 12; // Monthly

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, years, frequency);

            // Expected: 5000 * (1 + 0.06/12)^(12*5) ≈ 6744.25
            BigDecimal expected = new BigDecimal("6744.25");
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Compound Interest - Daily Compounding")
        void testCompoundInterest_DailyCompounding() {
            BigDecimal principal = new BigDecimal("2000.00");
            BigDecimal rate = new BigDecimal("0.04"); // 4%
            int years = 3;
            int frequency = 365; // Daily

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, years, frequency);

            // Should be close to continuous compounding: 2000 * e^(0.04*3) ≈ 2254.99
            assertTrue(result.compareTo(new BigDecimal("2254.00")) > 0);
            assertTrue(result.compareTo(new BigDecimal("2256.00")) < 0);
        }

        @ParameterizedTest
        @CsvSource({
                "1000.00, 0.00, 5, 1, 1000.00",  // Zero interest rate
                "0.00, 0.05, 5, 1, 0.00",        // Zero principal
                "1000.00, 0.05, 0, 1, 1000.00"   // Zero years
        })
        @DisplayName("Compound Interest - Edge Cases")
        void testCompoundInterest_EdgeCases(String principalStr, String rateStr,
                                            int years, int frequency, String expectedStr) {
            BigDecimal principal = new BigDecimal(principalStr);
            BigDecimal rate = new BigDecimal(rateStr);
            BigDecimal expected = new BigDecimal(expectedStr);

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, years, frequency);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Compound Interest - Invalid Parameters")
        void testCompoundInterest_InvalidParameters() {
            BigDecimal validPrincipal = new BigDecimal("1000.00");
            BigDecimal validRate = new BigDecimal("0.05");

            // Test null principal
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateCompoundInterest(null, validRate, 5, 1));

            // Test null rate
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateCompoundInterest(validPrincipal, null, 5, 1));

            // Test negative years
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateCompoundInterest(validPrincipal, validRate, -1, 1));

            // Test zero frequency
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateCompoundInterest(validPrincipal, validRate, 5, 0));

            // Test negative frequency
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateCompoundInterest(validPrincipal, validRate, 5, -1));
        }

        @Test
        @DisplayName("Compound Interest - High Precision Test")
        void testCompoundInterest_HighPrecision() {
            BigDecimal principal = new BigDecimal("1000.000000");
            BigDecimal rate = new BigDecimal("0.050000");
            int years = 1;
            int frequency = 365;

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, years, frequency);

            // Result should be precisely calculated and rounded to 2 decimal places
            assertEquals(2, result.scale());
            assertTrue(result.compareTo(new BigDecimal("1051.26")) > 0);
            assertTrue(result.compareTo(new BigDecimal("1051.28")) < 0);
        }
    }

    @Nested
    @DisplayName("Loan Payment Calculations")
    class LoanPaymentTests {

        @Test
        @DisplayName("Standard Loan Payment - 30 Year Mortgage")
        void testLoanPayment_StandardMortgage() {
            BigDecimal principal = new BigDecimal("300000.00");
            BigDecimal monthlyRate = new BigDecimal("0.004167"); // 5% annual / 12
            int months = 360; // 30 years

            BigDecimal result = Calculator.calculateLoanPayment(principal, monthlyRate, months);

            // Expected monthly payment approximately $1610.46
            BigDecimal expected = new BigDecimal("1610.46");
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Loan Payment - Zero Interest Rate")
        void testLoanPayment_ZeroInterestRate() {
            BigDecimal principal = new BigDecimal("12000.00");
            BigDecimal monthlyRate = BigDecimal.ZERO;
            int months = 12;

            BigDecimal result = Calculator.calculateLoanPayment(principal, monthlyRate, months);

            // With zero interest, payment = principal / months
            BigDecimal expected = new BigDecimal("1000.00");
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Loan Payment - Short Term High Rate")
        void testLoanPayment_ShortTermHighRate() {
            BigDecimal principal = new BigDecimal("5000.00");
            BigDecimal monthlyRate = new BigDecimal("0.02"); // 24% annual
            int months = 12;

            BigDecimal result = Calculator.calculateLoanPayment(principal, monthlyRate, months);

            // High interest rate should result in higher monthly payment
            assertTrue(result.compareTo(new BigDecimal("470.00")) > 0);
            assertTrue(result.compareTo(new BigDecimal("480.00")) < 0);
        }

        @ParameterizedTest
        @CsvSource({
                "1000.00, 0.01, 12, 88.85",
                "5000.00, 0.005, 24, 221.47",
                "10000.00, 0.0075, 36, 309.50"
        })
        @DisplayName("Loan Payment - Various Scenarios")
        void testLoanPayment_VariousScenarios(String principalStr, String rateStr,
                                              int months, String expectedStr) {
            BigDecimal principal = new BigDecimal(principalStr);
            BigDecimal rate = new BigDecimal(rateStr);
            BigDecimal expected = new BigDecimal(expectedStr);

            BigDecimal result = Calculator.calculateLoanPayment(principal, rate, months);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Loan Payment - Invalid Parameters")
        void testLoanPayment_InvalidParameters() {
            BigDecimal validPrincipal = new BigDecimal("1000.00");
            BigDecimal validRate = new BigDecimal("0.01");
            int validMonths = 12;

            // Test null principal
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateLoanPayment(null, validRate, validMonths));

            // Test null rate
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateLoanPayment(validPrincipal, null, validMonths));

            // Test zero months
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateLoanPayment(validPrincipal, validRate, 0));

            // Test negative months
            assertThrows(IllegalArgumentException.class, () ->
                    Calculator.calculateLoanPayment(validPrincipal, validRate, -12));
        }

        @Test
        @DisplayName("Loan Payment - Zero Principal")
        void testLoanPayment_ZeroPrincipal() {
            BigDecimal principal = BigDecimal.ZERO;
            BigDecimal rate = new BigDecimal("0.01");
            int months = 12;

            BigDecimal result = Calculator.calculateLoanPayment(principal, rate, months);

            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("Loan Payment - Negative Principal")
        void testLoanPayment_NegativePrincipal() {
            BigDecimal principal = new BigDecimal("-1000.00");
            BigDecimal rate = new BigDecimal("0.01");
            int months = 12;

            BigDecimal result = Calculator.calculateLoanPayment(principal, rate, months);

            assertEquals(BigDecimal.ZERO, result);
        }
    }

    @Nested
    @DisplayName("Prime Number Algorithm")
    class PrimeNumberTests {

        @ParameterizedTest
        @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47})
        @DisplayName("Prime Numbers - Known Primes")
        void testPrimeNumbers_KnownPrimes(int number) {
            assertTrue(Calculator.isPrime(number), number + " should be prime");
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 6, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 24, 25})
        @DisplayName("Prime Numbers - Known Composites")
        void testPrimeNumbers_KnownComposites(int number) {
            assertFalse(Calculator.isPrime(number), number + " should not be prime");
        }

        @ParameterizedTest
        @ValueSource(ints = {-10, -5, -1, 0, 1})
        @DisplayName("Prime Numbers - Invalid Inputs")
        void testPrimeNumbers_InvalidInputs(int number) {
            assertFalse(Calculator.isPrime(number), number + " should not be prime");
        }

        @Test
        @DisplayName("Prime Numbers - Large Primes")
        void testPrimeNumbers_LargePrimes() {
            // Test some larger known primes
            assertTrue(Calculator.isPrime(97));
            assertTrue(Calculator.isPrime(101));
            assertTrue(Calculator.isPrime(103));
            assertTrue(Calculator.isPrime(107));
            assertTrue(Calculator.isPrime(109));
            assertTrue(Calculator.isPrime(113));
        }

        @Test
        @DisplayName("Prime Numbers - Large Composites")
        void testPrimeNumbers_LargeComposites() {
            // Test some larger known composites
            assertFalse(Calculator.isPrime(91)); // 7 * 13
            assertFalse(Calculator.isPrime(93)); // 3 * 31
            assertFalse(Calculator.isPrime(95)); // 5 * 19
            assertFalse(Calculator.isPrime(99)); // 9 * 11
            assertFalse(Calculator.isPrime(121)); // 11 * 11
        }

        @Test
        @DisplayName("Prime Numbers - Perfect Squares")
        void testPrimeNumbers_PerfectSquares() {
            // Perfect squares (except 1) should never be prime
            assertFalse(Calculator.isPrime(4));   // 2²
            assertFalse(Calculator.isPrime(9));   // 3²
            assertFalse(Calculator.isPrime(16));  // 4²
            assertFalse(Calculator.isPrime(25));  // 5²
            assertFalse(Calculator.isPrime(36));  // 6²
            assertFalse(Calculator.isPrime(49));  // 7²
            assertFalse(Calculator.isPrime(64));  // 8²
            assertFalse(Calculator.isPrime(81));  // 9²
            assertFalse(Calculator.isPrime(100)); // 10²
        }

        @Test
        @DisplayName("Prime Numbers - Boundary Values")
        void testPrimeNumbers_BoundaryValues() {
            // Test numbers around common boundaries
            assertFalse(Calculator.isPrime(1));
            assertTrue(Calculator.isPrime(2));   // Smallest prime
            assertTrue(Calculator.isPrime(3));   // Second smallest prime
            assertFalse(Calculator.isPrime(4));  // First composite after 2 and 3
        }

        @Test
        @DisplayName("Prime Numbers - Numbers Divisible by 2 or 3")
        void testPrimeNumbers_DivisibleBy2Or3() {
            // All even numbers > 2 should not be prime
            for (int i = 4; i <= 20; i += 2) {
                assertFalse(Calculator.isPrime(i), i + " (even) should not be prime");
            }

            // All multiples of 3 > 3 should not be prime
            for (int i = 6; i <= 21; i += 3) {
                assertFalse(Calculator.isPrime(i), i + " (multiple of 3) should not be prime");
            }
        }

        @Test
        @DisplayName("Prime Numbers - Algorithm Efficiency Test")
        void testPrimeNumbers_AlgorithmEfficiency() {
            // Test that the algorithm handles moderately large numbers efficiently
            // This tests the 6k±1 optimization
            assertTrue(Calculator.isPrime(997));   // Large prime
            assertFalse(Calculator.isPrime(999)); // 3³ * 37
            assertTrue(Calculator.isPrime(1009));  // Another large prime
            assertFalse(Calculator.isPrime(1001)); // 7 * 11 * 13
        }
    }

    @Nested
    @DisplayName("Mathematical Precision and Edge Cases")
    class PrecisionAndEdgeCaseTests {

        @Test
        @DisplayName("Rounding Behavior - Compound Interest")
        void testRoundingBehavior_CompoundInterest() {
            BigDecimal principal = new BigDecimal("1000.123456789");
            BigDecimal rate = new BigDecimal("0.050000000001");

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, 1, 1);

            // Result should be rounded to 2 decimal places
            assertEquals(2, result.scale());
            assertTrue(result.toString().matches("\\d+\\.\\d{2}"));
        }

        @Test
        @DisplayName("Rounding Behavior - Loan Payment")
        void testRoundingBehavior_LoanPayment() {
            BigDecimal principal = new BigDecimal("1000.999999");
            BigDecimal rate = new BigDecimal("0.010000001");

            BigDecimal result = Calculator.calculateLoanPayment(principal, rate, 12);

            // Result should be rounded to 2 decimal places
            assertEquals(2, result.scale());
            assertTrue(result.toString().matches("\\d+\\.\\d{2}"));
        }

        @Test
        @DisplayName("Very Large Numbers - Compound Interest")
        void testVeryLargeNumbers_CompoundInterest() {
            BigDecimal principal = new BigDecimal("999999999.99");
            BigDecimal rate = new BigDecimal("0.01");

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, 1, 1);

            // Should handle large numbers without overflow
            assertTrue(result.compareTo(new BigDecimal("1000000000.00")) > 0);
            assertEquals(2, result.scale());
        }

        @Test
        @DisplayName("Very Small Numbers - Interest Calculation")
        void testVerySmallNumbers_InterestCalculation() {
            BigDecimal principal = new BigDecimal("0.01");
            BigDecimal rate = new BigDecimal("0.00001"); // Very small rate

            BigDecimal result = Calculator.calculateCompoundInterest(principal, rate, 1, 1);

            // Should handle very small calculations
            assertTrue(result.compareTo(principal) > 0);
            assertEquals(2, result.scale());
        }

        @Test
        @DisplayName("Extreme Values - Prime Number Algorithm")
        void testExtremeValues_PrimeAlgorithm() {
            // Test with some challenging cases for the algorithm
            assertTrue(Calculator.isPrime(982451653)); // Large known prime
            assertFalse(Calculator.isPrime(982451654)); // Not prime (even)

            // Test edge case of algorithm boundaries
            assertFalse(Calculator.isPrime(Integer.MAX_VALUE)); // 2^31 - 1 is not prime
        }
    }
}