package com.bankit.loan.controller;

import com.bankit.loan.model.AccountType;
import com.bankit.loan.model.Loan;
import com.bankit.loan.model.Officer;
import com.bankit.loan.service.OfficerService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import com.bankit.loan.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    @FXML private ComboBox<Officer> officerNameComboBox;
    @FXML private TextField officerEmailField;
    @FXML private TextField officerContactField;

    private MainController mainController;
    private OfficerService officerService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09\\d{9}$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTextFormatters();
        setupValidation();
        setupInitialValues();
        officerService = ServiceFactory.getInstance().getOfficerService();
        setupOfficerComboBox();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    private void setupTextFormatters() {
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };

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
        setupEmailValidation(emailField);
        setupEmailValidation(officerEmailField);
        setupPhoneValidation(contactField);
        setupPhoneValidation(officerContactField);
        setupRequiredFieldValidation(customerIdField, "Customer ID is required");
        setupRequiredFieldValidation(fullNameField, "Full name must be at least 2 characters");
        setupRequiredFieldValidation(addressField, "Address is required");
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
        accountTypeCombo.getItems().addAll(AccountType.values());
        accountTypeCombo.setPromptText("Select Account Type");
        issueDateField.setText(DateUtils.getCurrentDate());
        issueDateField.setEditable(false);
        loanIdField.setEditable(false);
    }

    private void setupOfficerComboBox() {
        try {
            List<Officer> officers = officerService.getAllOfficers();
            officerNameComboBox.setItems(FXCollections.observableArrayList(officers));
            officerNameComboBox.setEditable(true);

            // Set up string converter for Officer objects
            officerNameComboBox.setConverter(new StringConverter<Officer>() {
                @Override
                public String toString(Officer officer) {
                    if (officer == null) return null;
                    return officer.getName();
                }

                @Override
                public Officer fromString(String string) {
                    if (string == null || string.trim().isEmpty()) return null;
                    // Try to find existing officer
                    return officers.stream()
                            .filter(o -> o.getName().equalsIgnoreCase(string.trim()))
                            .findFirst()
                            .orElse(null); // Return null for new officers instead of creating one
                }
            });

            // Setup display in dropdown list
            officerNameComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Officer officer, boolean empty) {
                    super.updateItem(officer, empty);
                    if (empty || officer == null) {
                        setText(null);
                    } else {
                        setText(String.format("%s - %s", officer.getOfficerId(), officer.getName()));
                    }
                }
            });

            // Handle selection changes - only trigger for actual selections
            officerNameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.getOfficerId() != null) {
                    // Only handle existing officers selected from dropdown
                    officerIdField.setText(newVal.getOfficerId());
                    officerEmailField.setText(newVal.getEmail());
                    officerContactField.setText(newVal.getContact());
                    officerEmailField.setEditable(false);
                    officerContactField.setEditable(false);
                } else {
                    // For new officers or cleared selection
                    if (officerNameComboBox.getEditor().getText().trim().isEmpty()) {
                        // Clear everything if name is empty
                        officerIdField.clear();
                        officerEmailField.clear();
                        officerContactField.clear();
                    } else {
                        // Generate new ID for new officer name
                        String newOfficerId = ServiceFactory.getInstance().getOfficerService().generateOfficerId();
                        officerIdField.setText(newOfficerId);
                    }
                    officerEmailField.setEditable(true);
                    officerContactField.setEditable(true);
                }
            });

            // Add listener for manual text entry
            officerNameComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    boolean isExistingOfficer = officers.stream()
                            .anyMatch(o -> o.getName().equalsIgnoreCase(newVal.trim()));

                    if (!isExistingOfficer) {
                        // Enable editing for new officer
                        officerEmailField.setEditable(true);
                        officerContactField.setEditable(true);

                        if (officerIdField.getText().isEmpty()) {
                            String newOfficerId = ServiceFactory.getInstance().getOfficerService().generateOfficerId();
                            officerIdField.setText(newOfficerId);
                        }
                    }
                }
            });

            officerNameComboBox.setPromptText("Select or enter new officer name");

            // Set initial state
            officerNameComboBox.setValue(null);
            officerEmailField.setEditable(true);
            officerContactField.setEditable(true);

        } catch (SQLException e) {
            AlertUtils.showError("Database Error", "Failed to load officers: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateId() {
        String loanId = ServiceFactory.getInstance().getLoanService().generateLoanId();
        loanIdField.setText(loanId);
    }

    public Loan getLoanData() {
        try {
            Loan loan = new Loan();

            loan.setCustomerId(customerIdField.getText());
            loan.setCustomerName(fullNameField.getText());
            loan.setContact(contactField.getText());
            loan.setEmail(emailField.getText());
            loan.setAddress(addressField.getText());
            loan.setAccountType(accountTypeCombo.getValue());
            loan.setLoanId(loanIdField.getText());
            loan.setLoanAmount(Double.parseDouble(loanAmountField.getText()));
            loan.setInterestRate(Double.parseDouble(interestRateField.getText()));
            loan.setTermMonths(Integer.parseInt(termMonthsField.getText()));

            LocalDate issueDate = DateUtils.parseDate(issueDateField.getText());
            loan.setIssueDate(issueDate);
            loan.setDueDate(DateUtils.calculateDueDate(issueDate, loan.getTermMonths()));

            // Create Officer object properly
            Officer officer = new Officer();
            officer.setOfficerId(officerIdField.getText());

            // Handle officer name from ComboBox
            if (officerNameComboBox.getValue() != null) {
                // If an existing officer is selected
                Officer selectedOfficer = officerNameComboBox.getValue();
                officer.setName(selectedOfficer.getName());
                officer.setEmail(selectedOfficer.getEmail());
                officer.setContact(selectedOfficer.getContact());
            } else {
                // If a new officer name is entered
                officer.setName(officerNameComboBox.getEditor().getText());
                officer.setEmail(officerEmailField.getText());
                officer.setContact(officerContactField.getText());
            }

            loan.setOfficer(officer);

            return loan;
        } catch (Exception e) {
            AlertUtils.showError("Data Error", "Invalid data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Populates form fields with loan data
     * @param loan The loan data to populate
     */
    public void setLoanData(Loan loan) {
        if (loan == null) return;

        // Populate main loan details
        customerIdField.setText(loan.getCustomerId());
        fullNameField.setText(loan.getCustomerName());
        contactField.setText(loan.getContact());
        emailField.setText(loan.getEmail());
        addressField.setText(loan.getAddress());
        accountTypeCombo.setValue(loan.getAccountType());
        loanIdField.setText(loan.getLoanId());
        loanAmountField.setText(String.valueOf(loan.getLoanAmount()));
        interestRateField.setText(String.valueOf(loan.getInterestRate()));
        termMonthsField.setText(String.valueOf(loan.getTermMonths()));
        issueDateField.setText(DateUtils.formatDate(loan.getIssueDate()));

        // Populate officer details and make them read-only
        Officer officer = loan.getOfficer();
        if (officer != null) {
            officerIdField.setText(officer.getOfficerId());
            officerNameComboBox.setValue(officer);
            officerEmailField.setText(officer.getEmail());
            officerContactField.setText(officer.getContact());

            // Make officer fields read-only
            officerIdField.setEditable(false);
            officerNameComboBox.setDisable(true);
            officerEmailField.setEditable(false);
            officerContactField.setEditable(false);
        }
    }

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
        officerNameComboBox.setValue(null);
        officerNameComboBox.getEditor().clear();
        officerEmailField.clear();
        officerContactField.clear();

        // Re-enable officer fields
        officerIdField.setEditable(true);
        officerNameComboBox.setDisable(false);
        officerEmailField.setEditable(true);
        officerContactField.setEditable(true);

        // Reset any validation styling
        clearValidationStyles();
    }

    /**
     * Clears any validation-related styling from form fields
     */
    private void clearValidationStyles() {
        // Get all text fields in the form
        List<TextField> fields = Arrays.asList(
                customerIdField, fullNameField, contactField, emailField,
                addressField, loanIdField, loanAmountField, interestRateField,
                termMonthsField, officerIdField, officerEmailField, officerContactField
        );

        // Clear any error styles
        fields.forEach(field -> {
            field.setStyle("");
            field.setTooltip(null);
        });

        accountTypeCombo.setStyle("");
        accountTypeCombo.setTooltip(null);
        officerNameComboBox.setStyle("");
        officerNameComboBox.setTooltip(null);
    }
}