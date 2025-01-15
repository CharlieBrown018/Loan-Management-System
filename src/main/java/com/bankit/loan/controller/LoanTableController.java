package com.bankit.loan.controller;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.model.AccountType;
import com.bankit.loan.model.Loan;
import com.bankit.loan.model.Officer;
import com.bankit.loan.service.LoanService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import com.bankit.loan.util.DateUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the loan table section
 * Handles display and management of loan records in table format
 */
public class LoanTableController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button exportBtn;
    @FXML private Button importBtn;
    @FXML private TableView<Loan> loanTable;

    // Table Columns
    @FXML private TableColumn<Loan, String> loanIdColumn;
    @FXML private TableColumn<Loan, String> customerIdColumn;
    @FXML private TableColumn<Loan, String> customerNameColumn;
    @FXML private TableColumn<Loan, String> contactColumn;
    @FXML private TableColumn<Loan, String> emailColumn;
    @FXML private TableColumn<Loan, String> accountTypeColumn;
    @FXML private TableColumn<Loan, Double> loanAmountColumn;
    @FXML private TableColumn<Loan, Double> interestRateColumn;
    @FXML private TableColumn<Loan, Integer> termMonthsColumn;
    @FXML private TableColumn<Loan, Double> monthlyPaymentColumn;
    @FXML private TableColumn<Loan, Double> totalPaymentColumn;
    @FXML private TableColumn<Loan, String> issueDateColumn;
    @FXML private TableColumn<Loan, String> officerNameColumn;

    private LoanService loanService;
    private ObservableList<Loan> loans;
    private FilteredList<Loan> filteredLoans;
    private NumberFormat currencyFormat;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loanService = ServiceFactory.getInstance().getLoanService();
        currencyFormat = NumberFormat.getCurrencyInstance();
        setupTableColumns();
        setupSearchField();
        Platform.runLater(this::loadData);
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredLoans.setPredicate(loan ->
                    loan.getCustomerName().toLowerCase().contains(searchText) ||
                            loan.getLoanId().toLowerCase().contains(searchText)
            );
        });
    }

    private void setupTableColumns() {
        // Basic text columns
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        // Numeric columns
        loanAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        interestRateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        termMonthsColumn.setCellValueFactory(new PropertyValueFactory<>("termMonths"));
        monthlyPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        totalPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));

        // Currency formatting
        loanAmountColumn.setCellFactory(col -> new CurrencyTableCell());
        monthlyPaymentColumn.setCellFactory(col -> new CurrencyTableCell());
        totalPaymentColumn.setCellFactory(col -> new CurrencyTableCell());

        // Interest rate formatting
        interestRateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item));
                }
            }
        });

        // Date formatting
        issueDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(DateUtils.formatDateForDisplay(cellData.getValue().getIssueDate()))
        );

        // Officer name handling
        officerNameColumn.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            if (loan != null && loan.getOfficer() != null) {
                return new SimpleStringProperty(loan.getOfficer().getName());
            }
            return new SimpleStringProperty("");
        });

        // Row selection handler
        loanTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null && mainController != null) {
                        mainController.onLoanSelected(newSelection);
                        mainController.toggleEditMode(true);
                    }
                }
        );
    }

    private void loadData() {
        Task<List<Loan>> loadTask = new Task<>() {
            @Override
            protected List<Loan> call() throws Exception {
                return loanService.getAllLoans();
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<Loan> loanList = loadTask.getValue();
            Platform.runLater(() -> {
                try {
                    loans = FXCollections.observableArrayList(loanList);
                    filteredLoans = new FilteredList<>(loans);
                    loanTable.setItems(filteredLoans);
                    loanTable.refresh();
                } catch (Exception e) {
                    AlertUtils.showError("UI Update Error",
                            "Error updating table: " + e.getMessage());
                }
            });
        });

        loadTask.setOnFailed(event -> {
            AlertUtils.showError("Data Load Error",
                    "Error loading loan data: " + loadTask.getException().getMessage());
        });

        new Thread(loadTask).start();
    }

    /**
     * Handles the import button action with validation and error handling
     */
    @FXML
    private void handleImport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Loan Data");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                List<String> errorMessages = new ArrayList<>();
                int successCount = 0;
                int totalCount = 0;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String header = reader.readLine(); // Skip header
                    validateCSVHeader(header);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalCount++;
                        try {
                            Loan loan = parseCSVLine(line);
                            loanService.createLoan(loan);
                            successCount++;
                        } catch (Exception e) {
                            errorMessages.add(String.format("Row %d: %s", totalCount, e.getMessage()));
                        }
                    }

                    // Refresh table after import
                    loadData();

                    // Show results
                    if (errorMessages.isEmpty()) {
                        AlertUtils.showInfo("Import Success",
                                String.format("Successfully imported %d loan records", successCount));
                    } else {
                        StringBuilder message = new StringBuilder();
                        message.append(String.format("Imported %d of %d records successfully.\n\nErrors:\n",
                                successCount, totalCount));
                        errorMessages.forEach(err -> message.append("- ").append(err).append("\n"));
                        AlertUtils.showWarning("Import Completed with Errors", message.toString());
                    }
                }
            }
        } catch (Exception e) {
            AlertUtils.showError("Import Error", "Failed to import data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates CSV header format
     */
    private void validateCSVHeader(String header) {
        String expectedHeader = "Loan ID,Customer ID,Customer Name,Contact,Email,Address," +
                "Account Type,Loan Amount,Interest Rate,Term (Months)," +
                "Issue Date,Due Date,Monthly Payment,Total Payment," +
                "Officer ID,Officer Name,Officer Email";

        if (!header.replaceAll("\\s+", "").equalsIgnoreCase(expectedHeader.replaceAll("\\s+", ""))) {
            throw new IllegalArgumentException("Invalid CSV format. Please use the export format as template.");
        }
    }

    /**
     * Parses a CSV line into a Loan object
     */
    private Loan parseCSVLine(String line) throws Exception {
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split considering quoted values
        if (fields.length < 17) {
            throw new IllegalArgumentException("Invalid number of fields in CSV line");
        }

        Loan loan = new Loan();
        loan.setLoanId(fields[0].trim());
        loan.setCustomerId(fields[1].trim());
        loan.setCustomerName(unescapeCSV(fields[2]));
        loan.setContact(fields[3].trim());
        loan.setEmail(unescapeCSV(fields[4]));
        loan.setAddress(unescapeCSV(fields[5]));
        loan.setAccountType(AccountType.valueOf(fields[6].trim()));
        loan.setLoanAmount(Double.parseDouble(fields[7].trim()));
        loan.setInterestRate(Double.parseDouble(fields[8].trim()));
        loan.setTermMonths(Integer.parseInt(fields[9].trim()));
        loan.setIssueDate(DateUtils.parseDate(fields[10].trim()));
        loan.setDueDate(DateUtils.parseDate(fields[11].trim()));
        loan.setMonthlyPayment(Double.parseDouble(fields[12].trim()));
        loan.setTotalPayment(Double.parseDouble(fields[13].trim()));

        Officer officer = new Officer();
        officer.setOfficerId(fields[14].trim());
        officer.setName(unescapeCSV(fields[15]));
        officer.setEmail(unescapeCSV(fields[16]));
        // Default contact for imported officers if not available
        officer.setContact("09000000000");
        loan.setOfficer(officer);

        return loan;
    }

    /**
     * Unescapes CSV values
     */
    private String unescapeCSV(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.replace("\"\"", "\"").trim();
    }

    @FXML
    private void handleExport() {
        try {
            String timestamp = DateUtils.getCurrentDate().replaceAll("[^0-9]", "");
            String filename = String.format("BankIT_Loans_Export_%s.csv", timestamp);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Loan Data");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                exportToCSV(file);
                AlertUtils.showInfo("Success",
                        String.format("Successfully exported %d loan records", loans.size()));
            }
        } catch (Exception e) {
            AlertUtils.showError("Export Error", "Failed to export data: " + e.getMessage());
        }
    }

    private void exportToCSV(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("Loan ID,Customer ID,Customer Name,Contact,Email,Address," +
                    "Account Type,Loan Amount,Interest Rate,Term (Months)," +
                    "Issue Date,Due Date,Monthly Payment,Total Payment," +
                    "Officer ID,Officer Name,Officer Email");

            // Write data
            for (Loan loan : loans) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%d,%s,%s,%.2f,%.2f,%s,%s,%s%n",
                        escapeCSV(loan.getLoanId()),
                        escapeCSV(loan.getCustomerId()),
                        escapeCSV(loan.getCustomerName()),
                        escapeCSV(loan.getContact()),
                        escapeCSV(loan.getEmail()),
                        escapeCSV(loan.getAddress()),
                        loan.getAccountType(),
                        loan.getLoanAmount(),
                        loan.getInterestRate(),
                        loan.getTermMonths(),
                        loan.getIssueDate(),
                        loan.getDueDate(),
                        loan.getMonthlyPayment(),
                        loan.getTotalPayment(),
                        escapeCSV(loan.getOfficer().getOfficerId()),
                        escapeCSV(loan.getOfficer().getName()),
                        escapeCSV(loan.getOfficer().getEmail())
                );
            }
        }

        // Optionally open the generated csv file
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        value = value.replace("\"", "\"\"");
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void clearSelection() {
        loanTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                loadData();
                return null;
            }
        };

        refreshTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                AlertUtils.showError("Refresh Error",
                        "Failed to refresh data: " + refreshTask.getException().getMessage());
            });
        });

        new Thread(refreshTask).start();
    }

    public void refreshTable() {
        loadData();
    }

    public String getSelectedLoanId() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        return selectedLoan != null ? selectedLoan.getLoanId() : null;
    }

    private class CurrencyTableCell extends TableCell<Loan, Double> {
        @Override
        protected void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(currencyFormat.format(item));
            }
        }
    }
}