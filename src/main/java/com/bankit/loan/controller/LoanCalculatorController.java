package com.bankit.loan.controller;

import com.bankit.loan.model.Loan;
import com.bankit.loan.service.LoanService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import com.bankit.loan.util.DateUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Controller for the loan calculator section
 */
public class LoanCalculatorController implements Initializable {

    @FXML private TextField monthlyPaymentField;
    @FXML private TextField totalPaymentField;
    @FXML private TextArea summaryArea;
    @FXML private Button printSummaryBtn;
    @FXML private Button exportPdfBtn;

    private LoanService loanService;
    private NumberFormat currencyFormat;
    private double[] currentCalculations;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loanService = ServiceFactory.getInstance().getLoanService();
        currencyFormat = NumberFormat.getCurrencyInstance();
        setupTextFields();
    }

    private void setupTextFields() {
        // Set currency format for result fields
        monthlyPaymentField.setText(currencyFormat.format(0.0));
        totalPaymentField.setText(currencyFormat.format(0.0));
    }

    /**
     * Calculates loan payments based on form data
     */
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
            // Create print job
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();

            if (job != null && job.showPrintDialog(null)) {
                boolean printed = job.printPage(summaryArea);
                if (printed) {
                    job.endJob();
                    AlertUtils.showInfo("Success", "Summary printed successfully!");
                } else {
                    AlertUtils.showError("Print Error", "Printing failed");
                }
            }
        } catch (Exception e) {
            AlertUtils.showError("Print Error", "Error printing summary: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(summaryArea.getText());
                AlertUtils.showInfo("Success", "Summary exported successfully!");
            } catch (Exception e) {
                AlertUtils.showError("Export Error", "Error exporting summary: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the current calculations
     * @return array containing [monthlyPayment, totalPayment]
     */
    public double[] getCalculations() {
        return currentCalculations;
    }

    /**
     * Sets the calculator data from a loan object
     */
    public void setLoanData(Loan loan) {
        calculate(loan.getLoanAmount(), loan.getInterestRate(), loan.getTermMonths());
    }

    /**
     * Resets the calculator
     */
    public void reset() {
        monthlyPaymentField.setText(currencyFormat.format(0.0));
        totalPaymentField.setText(currencyFormat.format(0.0));
        summaryArea.clear();
        currentCalculations = null;
    }
}