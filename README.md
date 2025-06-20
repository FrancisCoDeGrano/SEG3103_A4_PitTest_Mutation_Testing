# SEG3103 Assignment 4 — PIT Mutation Testing Project

This project is a simple Java-based banking system designed to demonstrate **unit testing** and **mutation testing** using the **PIT (Pitest)** mutation testing tool in IntelliJ IDEA.

## 📁 Project Structure

SEG3103_A4_PitTest_Mutation_Testing/
├── src/
│ ├── main/java/com/banking/
│ │ ├── Account.java
│ │ ├── AccountType.java
│ │ ├── Calculator.java
│ │ ├── Transaction.java
│ │ └── TransactionType.java
│ └── test/java/com/banking/
│ ├── BasicAccountOperationsTest.java
│ ├── AdvancedAccountFeaturesTest.java
│ └── CalculatorAndComplexLogicTest.java
├── pom.xml
└── README.md


---

## 🧪 Testing Overview

### ✅ Unit Testing
JUnit 5 is used to verify the correctness of the banking system's behavior through tests such as:
- Basic account deposits and withdrawals
- Account balance verification
- Advanced features like overdraft handling
- Complex logic computations with Calculator

### 🔬 Mutation Testing (PIT)
[PITest](https://pitest.org/) is used to measure the effectiveness of our tests. It simulates small faults (mutations) in the code and reports whether the test suite detects them.

#### Key Goals:
- Ensure high test coverage
- Detect weak or missing assertions
- Encourage robust and meaningful tests

---

## 🧰 Prerequisites

- Java 11+
- IntelliJ IDEA
- Maven
- PIT Plugin for IntelliJ (installed via Plugins Marketplace)

---

## ▶️ Running Mutation Tests

1. **Open IntelliJ** and load the project.
2. Ensure that your Maven project is properly imported.
3. Go to **Run > Edit Configurations…**
4. Add a new **PIT Runner** configuration:
    - **Target classes**: `com.banking.*`
    - **Target tests**: `com.banking.BasicAccountOperationsTest,com.banking.AdvancedAccountFeaturesTest,com.banking.CalculatorAndComplexLogicTest`
    - **Source directory**: `src/main/java`
    - **Report directory**: `target/report`
    - **Other params**:
      ```
      --outputFormats XML,HTML --mutators DEFAULTS,STRONGER
      ```
5. Click **Run** or use the green play button.

6. View the mutation testing report:
    - Navigate to `target/report/index.html` and open it in a browser.

---

## 📊 Sample Mutation Report Output

- **Mutation Coverage**: e.g., 85%
- **Killed Mutants**: e.g., 42
- **Survived Mutants**: e.g., 6
- **No Coverage**: e.g., 2
- **Equivalent Mutants**: e.g., 1

Use these results to **improve your tests** by adding missing assertions or testing unhandled paths.

---

## 📌 Notes

- Avoid testing only "happy paths." Mutation testing is most effective when edge cases and unexpected behavior are covered.
- If a mutant **survives**, it means a fault in your code would not have been caught by the test suite.
- Refactor your tests to kill as many mutants as possible.

---

## 👨‍💻 Author

Francisco De Grano  
Course: SEG3103 – Software Testing  
Assignment: A4 — Mutation Testing

---

## 📄 License

This project is for educational purposes under the guidelines of the University of Ottawa through the joint program offered by Carleton University.
