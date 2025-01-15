package com.bankit.loan.controller;

import com.bankit.loan.model.Loan;
import com.bankit.loan.service.LoanService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import com.bankit.loan.util.DateUtils;
import com.bankit.loan.util.DocumentGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Controller for the loan calculator section
 */
/**
 * Controller for the loan calculator section
 * Handles calculations and document generation
 */
public class LoanCalculatorController implements Initializable {

    @FXML private TextField monthlyPaymentField;
    @FXML private TextField totalPaymentField;
    @FXML private TextArea summaryArea;
    @FXML private Button printSummaryBtn;
    @FXML private Button exportPdfBtn;

    private MainController mainController;
    private LoanFormController formController;
    private LoanService loanService;
    private NumberFormat currencyFormat;
    private double[] currentCalculations;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loanService = ServiceFactory.getInstance().getLoanService();
        currencyFormat = NumberFormat.getCurrencyInstance();
        setupTextFields();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setFormController(LoanFormController controller) {
        this.formController = controller;
    }

    private void setupTextFields() {
        monthlyPaymentField.setText(currencyFormat.format(0.0));
        totalPaymentField.setText(currencyFormat.format(0.0));
    }

    public void calculate(double principal, double interestRate, int termMonths) {
        try {
            currentCalculations = loanService.calculateLoanPayments(principal, interestRate, termMonths);
            monthlyPaymentField.setText(currencyFormat.format(currentCalculations[0]));
            totalPaymentField.setText(currencyFormat.format(currentCalculations[1]));
            updateSummary(principal, interestRate, termMonths);
        } catch (Exception e) {
            AlertUtils.showError("Calculation Error", e.getMessage());
        }
    }

    private void updateSummary(double principal, double interestRate, int termMonths) {
        StringBuilder summary = new StringBuilder();
        summary.append("LOAN CALCULATION SUMMARY\n");
        summary.append("========================\n\n");
        summary.append(String.format("Principal Amount: %s\n", currencyFormat.format(principal)));
        summary.append(String.format("Interest Rate: %.2f%%\n", interestRate));
        summary.append(String.format("Loan Term: %d months\n\n", termMonths));
        summary.append(String.format("Monthly Payment: %s\n", monthlyPaymentField.getText()));
        summary.append(String.format("Total Payment: %s\n", totalPaymentField.getText()));
        summary.append(String.format("Total Interest: %s\n\n",
                currencyFormat.format(currentCalculations[1] - principal)));
        summary.append("Date: ").append(DateUtils.getCurrentDateForDisplay());

        summaryArea.setText(summary.toString());
    }

    @FXML
    private void handlePrintSummary() {
        try {
            Loan loan = formController.getLoanData();
            if (loan == null) return;

            // Ensure calculations are done before printing
            calculate(loan.getLoanAmount(), loan.getInterestRate(), loan.getTermMonths());
            loan.setMonthlyPayment(currentCalculations[0]);
            loan.setTotalPayment(currentCalculations[1]);

            // Generate standardized filename
            String timestamp = DateUtils.getCurrentDate().replaceAll("[^0-9]", "");
            String filename = String.format("LOAN_AGREEMENT_%s_%s_%s.pdf",
                    loan.getLoanId(),
                    loan.getCustomerName().replaceAll("\\s+", "_"),
                    timestamp);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Loan Agreement");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                DocumentGenerator.generateCustomerLoanDocument(loan, file);
                AlertUtils.showInfo("Success", "Loan agreement generated successfully!");

                // Optionally open the generated PDF
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception e) {
            AlertUtils.showError("Document Generation Error", e.getMessage());
        }
    }

    @FXML
    private void handleExportPdf() {
        try {
            Loan loan = formController.getLoanData();
            if (loan == null) return;

            // Ensure calculations are done before printing
            calculate(loan.getLoanAmount(), loan.getInterestRate(), loan.getTermMonths());
            loan.setMonthlyPayment(currentCalculations[0]);
            loan.setTotalPayment(currentCalculations[1]);

            // Generate standardized filename for internal document
            String timestamp = DateUtils.getCurrentDate().replaceAll("[^0-9]", "");
            String filename = String.format("INTERNAL_LOAN_DOC_%s_%s_%s.pdf",
                    loan.getLoanId(),
                    loan.getOfficer().getOfficerId(),
                    timestamp);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Internal Loan Document");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                DocumentGenerator.generateBankLoanDocument(loan, file);
                AlertUtils.showInfo("Success", "Internal document generated successfully!");

                // Optionally open the generated PDF
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception e) {
            AlertUtils.showError("Document Generation Error", e.getMessage());
        }
    }

    public double[] getCalculations() {
        return currentCalculations;
    }

    public void setLoanData(Loan loan) {
        calculate(loan.getLoanAmount(), loan.getInterestRate(), loan.getTermMonths());
    }

    public void reset() {
        monthlyPaymentField.setText(currencyFormat.format(0.0));
        totalPaymentField.setText(currencyFormat.format(0.0));
        summaryArea.clear();
        currentCalculations = null;
    }
}