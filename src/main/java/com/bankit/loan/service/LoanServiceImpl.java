package com.bankit.loan.service;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.dao.DAOFactory;
import com.bankit.loan.dao.LoanDAO;
import com.bankit.loan.model.Loan;
import com.bankit.loan.model.Officer;
import com.bankit.loan.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Implementation of LoanService interface
 */
public class LoanServiceImpl implements LoanService {

    private final LoanDAO loanDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^09\\d{9}$"
    );

    public LoanServiceImpl() {
        this.loanDAO = DAOFactory.getInstance().getLoanDAO();
    }

    @Override
    public void createLoan(Loan loan) throws SQLException {
        validateLoan(loan);

        DatabaseConfig.ensureConnection();
        DatabaseConfig.beginTransaction();

        try {
            Officer officer = loan.getOfficer();
            Optional<Officer> existingOfficer = DAOFactory.getInstance()
                    .getOfficerDAO()
                    .findById(officer.getOfficerId());

            if (existingOfficer.isEmpty()) {
                DAOFactory.getInstance()
                        .getOfficerDAO()
                        .save(officer);
            }

            if (loan.getLoanId() == null || loan.getLoanId().trim().isEmpty()) {
                loan.setLoanId(generateLoanId());
            }

            loanDAO.save(loan);
            DatabaseConfig.commitTransaction();
        } catch (SQLException e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException {
        validateLoan(loan);
        loanDAO.update(loan);
    }

    @Override
    public void deleteLoan(String loanId) throws SQLException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan ID cannot be empty");
        }
        loanDAO.delete(loanId);
    }

    @Override
    public Loan getLoanById(String loanId) throws SQLException {
        return loanDAO.findById(loanId).orElse(null);
    }

    @Override
    public List<Loan> getAllLoans() throws SQLException {
        DatabaseConfig.ensureConnection();
        return loanDAO.findAll();
    }

    @Override
    public List<Loan> searchLoansByCustomerName(String customerName) throws SQLException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        return loanDAO.findByCustomerName(customerName.trim());
    }

    @Override
    public double[] calculateLoanPayments(double principal, double annualInterestRate, int termInMonths) {
        validateLoanCalculationInput(principal, annualInterestRate, termInMonths);

        double monthlyRate = (annualInterestRate/100) / 12;
        double monthlyPayment = (principal * monthlyRate * Math.pow(1 + monthlyRate, termInMonths))
                / (Math.pow(1 + monthlyRate, termInMonths) - 1);
        double totalPayment = monthlyPayment * termInMonths;

        return new double[]{monthlyPayment, totalPayment};
    }

    @Override
    public String generateLoanId() {
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000); // 5-digit number
        return "L" + randomNum;
    }

    /**
     * Validates loan data
     *
     * @param loan the loan to validate
     * @throws IllegalArgumentException if data is invalid
     */
    private void validateLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }

        ValidationUtils.validateNotEmpty(loan.getCustomerId(), "Customer ID");
        ValidationUtils.validateNotEmpty(loan.getCustomerName(), "Customer name");

        if (!PHONE_PATTERN.matcher(loan.getContact()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (!EMAIL_PATTERN.matcher(loan.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        ValidationUtils.validateNotEmpty(loan.getAddress(), "Address");

        if (loan.getAccountType() == null) {
            throw new IllegalArgumentException("Account type must be selected");
        }

        if (loan.getLoanAmount() <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than 0");
        }

        if (loan.getInterestRate() < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }

        if (loan.getTermMonths() <= 0) {
            throw new IllegalArgumentException("Loan term must be greater than 0");
        }

        if (loan.getOfficer() == null) {
            throw new IllegalArgumentException("Loan officer must be assigned");
        }
    }

    /**
     * Validates loan calculation input
     */
    private void validateLoanCalculationInput(double principal, double annualInterestRate, int termInMonths) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Principal amount must be greater than 0");
        }
        if (annualInterestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (termInMonths <= 0) {
            throw new IllegalArgumentException("Loan term must be greater than 0");
        }
    }
}