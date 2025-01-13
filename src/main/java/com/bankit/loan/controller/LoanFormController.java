package com.bankit.loan.controller;

import com.bankit.loan.model.AccountType;
import com.bankit.loan.model.Loan;
import com.bankit.loan.model.Officer;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.DateUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.function.UnaryOperator;

public class LoanFormController implements Initializable {

    @FXML private TextField customerIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private ComboBox<AccountType> accountTypeCombo;
    @FXML private TextField loanIdField;
    @FXML private TextField loanAmountField;
    @FXML private TextField interestRateField;
    @FXML private TextField termMonthsField;
    @FXML private TextField issueDateField;
    @FXML private TextField officerIdField;
    @FXML private TextField officerNameField;
    @FXML private TextField officerEmailField;
    @FXML private TextField officerContactField;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09\\d{9}$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTextFormatters();
        setupValidation();
        setupInitialValues();
    }

    private void setupTextFormatters() {
        // Numeric formatter for amount and rate
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };

        // Integer formatter for term months
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        loanAmountField.setTextFormatter(new TextFormatter<>(numericFilter));
        interestRateField.setTextFormatter(new TextFormatter<>(numericFilter));
        termMonthsField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    private void setupValidation() {
        // Email validation
        setupEmailValidation(emailField);
        setupEmailValidation(officerEmailField);

        // Phone validation
        setupPhoneValidation(contactField);
        setupPhoneValidation(officerContactField);

        // Required fields validation
        setupRequiredFieldValidation(customerIdField, "Customer ID is required");
        setupRequiredFieldValidation(fullNameField, "Full name must be at least 2 characters");
        setupRequiredFieldValidation(addressField, "Address is required");
        setupRequiredFieldValidation(officerIdField, "Officer ID is required");
        setupRequiredFieldValidation(officerNameField, "Officer name must be at least 2 characters");
    }

    private void setupEmailValidation(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!EMAIL_PATTERN.matcher(newVal).matches()) {
                field.setStyle("-fx-border-color: red;");
                field.setTooltip(new Tooltip("Invalid email format"));
            } else {
                field.setStyle("");
                field.setTooltip(null);
            }
        });
    }

    private void setupPhoneValidation(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!PHONE_PATTERN.matcher(newVal).matches()) {
                field.setStyle("-fx-border-color: red;");
                field.setTooltip(new Tooltip("Phone must start with 09 and be 11 digits"));
            } else {
                field.setStyle("");
                field.setTooltip(null);
            }
        });
    }

    private void setupRequiredFieldValidation(TextField field, String message) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty() ||
                    (message.contains("2 characters") && newVal.trim().length() < 2)) {
                field.setStyle("-fx-border-color: red;");
                field.setTooltip(new Tooltip(message));
            } else {
                field.setStyle("");
                field.setTooltip(null);
            }
        });
    }

    private void setupInitialValues() {
        // Setup account type combo box
        accountTypeCombo.getItems().addAll(AccountType.values());
        accountTypeCombo.setPromptText("Select Account Type");

        // Set current date
        issueDateField.setText(DateUtils.getCurrentDate());
        issueDateField.setEditable(false);

        // Set loan ID field as non-editable
        loanIdField.setEditable(false);
    }

    @FXML
    private void handleGenerateId() {
        String loanId = ServiceFactory.getInstance().getLoanService().generateLoanId();
        loanIdField.setText(loanId);
    }

    /**
     * Gets the loan data from the form
     */
    public Loan getLoanData() {
        Loan loan = new Loan();

        // Set customer information
        loan.setCustomerId(customerIdField.getText());
        loan.setCustomerName(fullNameField.getText());
        loan.setContact(contactField.getText());
        loan.setEmail(emailField.getText());
        loan.setAddress(addressField.getText());
        loan.setAccountType(accountTypeCombo.getValue());

        // Set loan information
        loan.setLoanId(loanIdField.getText());
        loan.setLoanAmount(Double.parseDouble(loanAmountField.getText()));
        loan.setInterestRate(Double.parseDouble(interestRateField.getText()));
        loan.setTermMonths(Integer.parseInt(termMonthsField.getText()));
        loan.setIssueDate(DateUtils.parseDate(issueDateField.getText()));

        // Set officer information
        Officer officer = new Officer();
        officer.setOfficerId(officerIdField.getText());
        officer.setName(officerNameField.getText());
        officer.setEmail(officerEmailField.getText());
        officer.setContact(officerContactField.getText());
        loan.setOfficer(officer);

        return loan;
    }

    /**
     * Sets the form data from a loan object
     */
    public void setLoanData(Loan loan) {
        // Set customer information
        customerIdField.setText(loan.getCustomerId());
        fullNameField.setText(loan.getCustomerName());
        contactField.setText(loan.getContact());
        emailField.setText(loan.getEmail());
        addressField.setText(loan.getAddress());
        accountTypeCombo.setValue(loan.getAccountType());

        // Set loan information
        loanIdField.setText(loan.getLoanId());
        loanAmountField.setText(String.valueOf(loan.getLoanAmount()));
        interestRateField.setText(String.valueOf(loan.getInterestRate()));
        termMonthsField.setText(String.valueOf(loan.getTermMonths()));
        issueDateField.setText(DateUtils.formatDate(loan.getIssueDate()));

        // Set officer information
        Officer officer = loan.getOfficer();
        officerIdField.setText(officer.getOfficerId());
        officerNameField.setText(officer.getName());
        officerEmailField.setText(officer.getEmail());
        officerContactField.setText(officer.getContact());
    }

    /**
     * Resets the form to its initial state
     */
    public void resetForm() {
        customerIdField.clear();
        fullNameField.clear();
        contactField.clear();
        emailField.clear();
        addressField.clear();
        accountTypeCombo.setValue(null);

        loanIdField.clear();
        loanAmountField.clear();
        interestRateField.clear();
        termMonthsField.clear();
        issueDateField.setText(DateUtils.getCurrentDate());

        officerIdField.clear();
        officerNameField.clear();
        officerEmailField.clear();
        officerContactField.clear();
    }
}