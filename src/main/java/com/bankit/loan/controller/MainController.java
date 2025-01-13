package com.bankit.loan.controller;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.model.Loan;
import com.bankit.loan.service.LoanService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main application window
 * Manages interactions between form, calculator, and table components
 */
public class MainController implements Initializable {

    @FXML private Label headerLabel;
    @FXML private Button calculateBtn;
    @FXML private Button saveBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button resetBtn;

    private LoanService loanService;
    private LoanFormController formController;
    private LoanCalculatorController calculatorController;
    private LoanTableController tableController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize service
        loanService = ServiceFactory.getInstance().getLoanService();

        // Set header text
        headerLabel.setText("BankIT Loan Management System");

        // Initial button states
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }

    @FXML
    private void handleCalculate() {
        if (formController == null || calculatorController == null) {
            AlertUtils.showError("System Error", "Application controllers not properly initialized");
            return;
        }

        try {
            Loan loan = formController.getLoanData();
            if (loan == null) {
                AlertUtils.showWarning("Validation Error", "Please fill in all required fields");
                return;
            }

            calculatorController.calculate(
                    loan.getLoanAmount(),
                    loan.getInterestRate(),
                    loan.getTermMonths()
            );
        } catch (NumberFormatException e) {
            AlertUtils.showError("Input Error", "Please enter valid numeric values");
        } catch (Exception e) {
            AlertUtils.showError("Calculation Error", e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (formController == null || calculatorController == null || tableController == null) {
            AlertUtils.showError("System Error", "Application controllers not properly initialized");
            return;
        }

        try {
            Loan loan = formController.getLoanData();
            if (loan == null) {
                AlertUtils.showWarning("Validation Error", "Please fill in all required fields");
                return;
            }

            double[] calculations = calculatorController.getCalculations();
            if (calculations == null) {
                AlertUtils.showWarning("Calculation Required", "Please calculate the loan first");
                return;
            }

            loan.setMonthlyPayment(calculations[0]);
            loan.setTotalPayment(calculations[1]);

            loanService.createLoan(loan);

            // Force refresh after save
            DatabaseConfig.closeConnection();
            tableController.refreshTable();
            handleReset();

            AlertUtils.showInfo("Success", "Loan record saved successfully!");
        } catch (Exception e) {
            AlertUtils.showError("Save Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (formController == null || calculatorController == null || tableController == null) {
            AlertUtils.showError("System Error", "Application controllers not properly initialized");
            return;
        }

        try {
            Loan loan = formController.getLoanData();
            if (loan == null) {
                AlertUtils.showWarning("Validation Error", "Please fill in all required fields");
                return;
            }

            double[] calculations = calculatorController.getCalculations();
            if (calculations == null) {
                AlertUtils.showWarning("Calculation Required", "Please calculate the loan first");
                return;
            }

            loan.setMonthlyPayment(calculations[0]);
            loan.setTotalPayment(calculations[1]);

            loanService.updateLoan(loan);
            tableController.refreshTable();

            AlertUtils.showInfo("Success", "Loan record updated successfully!");
        } catch (Exception e) {
            AlertUtils.showError("Update Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (tableController == null) {
            AlertUtils.showError("System Error", "Application controllers not properly initialized");
            return;
        }

        String loanId = tableController.getSelectedLoanId();
        if (loanId == null || loanId.trim().isEmpty()) {
            AlertUtils.showWarning("Selection Required", "Please select a loan to delete");
            return;
        }

        if (AlertUtils.showConfirmation("Delete Record", "Are you sure you want to delete this loan record?")) {
            try {
                loanService.deleteLoan(loanId);
                tableController.refreshTable();
                handleReset();

                AlertUtils.showInfo("Success", "Loan record deleted successfully!");
            } catch (Exception e) {
                AlertUtils.showError("Delete Error", e.getMessage());
            }
        }
    }

    @FXML
    private void handleReset() {
        if (formController != null) {
            formController.resetForm();
        }
        if (calculatorController != null) {
            calculatorController.reset();
        }
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }

    /**
     * Called when a loan is selected from the table
     * @param loan The selected loan
     */
    public void onLoanSelected(Loan loan) {
        if (loan == null) return;

        if (formController != null) {
            formController.setLoanData(loan);
        }
        if (calculatorController != null) {
            calculatorController.setLoanData(loan);
        }
        updateBtn.setDisable(false);
        deleteBtn.setDisable(false);
    }

    // Setter methods for child controllers
    public void setFormController(LoanFormController controller) {
        this.formController = controller;
    }

    public void setCalculatorController(LoanCalculatorController controller) {
        this.calculatorController = controller;
    }

    public void setTableController(LoanTableController controller) {
        this.tableController = controller;
    }
}