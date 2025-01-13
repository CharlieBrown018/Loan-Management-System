package com.bankit.loan.dao;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.model.AccountType;
import com.bankit.loan.model.Loan;
import com.bankit.loan.model.Officer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of LoanDAO interface
 */
public class LoanDAOImpl implements LoanDAO {

    private final OfficerDAO officerDAO;

    public LoanDAOImpl(OfficerDAO officerDAO) {
        this.officerDAO = officerDAO;
    }

    @Override
    public void save(Loan loan) throws SQLException {
        String sql = """
            INSERT INTO loans (
                loan_id, customer_id, customer_name, contact, email, address, 
                account_type, loan_amount, interest_rate, term_months, 
                issue_date, due_date, monthly_payment, total_payment, officer_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setLoanParameters(pstmt, loan);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Loan loan) throws SQLException {
        String sql = """
            UPDATE loans SET 
                customer_id = ?, customer_name = ?, contact = ?, email = ?, 
                address = ?, account_type = ?, loan_amount = ?, interest_rate = ?, 
                term_months = ?, issue_date = ?, due_date = ?, monthly_payment = ?, 
                total_payment = ?, officer_id = ?
            WHERE loan_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setLoanParameters(pstmt, loan);
            pstmt.setString(15, loan.getLoanId());

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(String loanId) throws SQLException {
        String sql = "DELETE FROM loans WHERE loan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loanId);

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
        }
    }

    @Override
    public Optional<Loan> findById(String loanId) throws SQLException {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loanId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(resultSetToLoan(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(resultSetToLoan(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findByCustomerName(String customerName) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE customer_name LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + customerName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(resultSetToLoan(rs));
                }
            }
        }
        return loans;
    }

    private void setLoanParameters(PreparedStatement pstmt, Loan loan) throws SQLException {
        pstmt.setString(1, loan.getLoanId());
        pstmt.setString(2, loan.getCustomerId());
        pstmt.setString(3, loan.getCustomerName());
        pstmt.setString(4, loan.getContact());
        pstmt.setString(5, loan.getEmail());
        pstmt.setString(6, loan.getAddress());
        pstmt.setString(7, loan.getAccountType().name());
        pstmt.setDouble(8, loan.getLoanAmount());
        pstmt.setDouble(9, loan.getInterestRate());
        pstmt.setInt(10, loan.getTermMonths());
        pstmt.setString(11, loan.getIssueDate().toString());
        pstmt.setString(12, loan.getDueDate().toString());
        pstmt.setDouble(13, loan.getMonthlyPayment());
        pstmt.setDouble(14, loan.getTotalPayment());
        pstmt.setString(15, loan.getOfficer().getOfficerId());
    }

    private Loan resultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getString("loan_id"));
        loan.setCustomerId(rs.getString("customer_id"));
        loan.setCustomerName(rs.getString("customer_name"));
        loan.setContact(rs.getString("contact"));
        loan.setEmail(rs.getString("email"));
        loan.setAddress(rs.getString("address"));
        loan.setAccountType(AccountType.valueOf(rs.getString("account_type")));
        loan.setLoanAmount(rs.getDouble("loan_amount"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setTermMonths(rs.getInt("term_months"));
        loan.setIssueDate(LocalDate.parse(rs.getString("issue_date")));
        loan.setDueDate(LocalDate.parse(rs.getString("due_date")));
        loan.setMonthlyPayment(rs.getDouble("monthly_payment"));
        loan.setTotalPayment(rs.getDouble("total_payment"));

        // Load the associated officer
        String officerId = rs.getString("officer_id");
        Optional<Officer> officer = officerDAO.findById(officerId);
        officer.ifPresent(loan::setOfficer);

        return loan;
    }
}