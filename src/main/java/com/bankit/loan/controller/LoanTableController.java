package com.bankit.loan.controller;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.model.Loan;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the loan table section.
 * Handles the display and management of loan records in a table format.
 */
public class LoanTableController implements Initializable {

    // FXML Injected Controls
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

    // Controller State
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

        // Load data asynchronously to prevent UI freezing
        Platform.runLater(this::loadData);
    }

    /**
     * Sets up all table columns with appropriate cell factories and value factories
     */
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

        // Currency formatting for monetary values
        loanAmountColumn.setCellFactory(col -> new CurrencyTableCell());
        monthlyPaymentColumn.setCellFactory(col -> new CurrencyTableCell());
        totalPaymentColumn.setCellFactory(col -> new CurrencyTableCell());

        // Percentage formatting for interest rate
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

        // Date formatting for issue date
        issueDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(DateUtils.formatDateForDisplay(cellData.getValue().getIssueDate())));

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
                    if (newSelection != null) {
                        mainController.onLoanSelected(newSelection);
                    }
                }
        );

        // Add debug row factory
        loanTable.setRowFactory(tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Loan loan = row.getItem();
                    debugPrintLoan(loan);
                }
            });
            return row;
        });
    }

    /**
     * Sets up the search functionality
     */
    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredLoans.setPredicate(loan ->
                    loan.getCustomerName().toLowerCase().contains(searchText) ||
                            loan.getLoanId().toLowerCase().contains(searchText)
            );
        });
    }

    /**
     * Loads loan data asynchronously
     */
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

                    // Debug output
                    System.out.println("Successfully loaded " + loanList.size() + " loans");
                    loanList.forEach(this::debugPrintLoan);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showError("UI Update Error",
                            "Error updating table: " + e.getMessage());
                }
            });
        });

        loadTask.setOnFailed(event -> {
            Throwable exception = loadTask.getException();
            Platform.runLater(() -> {
                AlertUtils.showError("Data Load Error",
                        "Error loading loan data: " + exception.getMessage());
            });
            exception.printStackTrace();
        });

        new Thread(loadTask).start();
    }

    /**
     * Debug method to print loan details
     */
    private void debugPrintLoan(Loan loan) {
        System.out.println("Loan Details:");
        System.out.println("ID: " + loan.getLoanId());
        System.out.println("Customer: " + loan.getCustomerName());
        System.out.println("Amount: " + currencyFormat.format(loan.getLoanAmount()));
        System.out.println("Interest: " + String.format("%.2f%%", loan.getInterestRate()));
        System.out.println("Monthly Payment: " + currencyFormat.format(loan.getMonthlyPayment()));
        System.out.println("Total Payment: " + currencyFormat.format(loan.getTotalPayment()));
        System.out.println("Issue Date: " + loan.getIssueDate());
        System.out.println("Officer: " + (loan.getOfficer() != null ? loan.getOfficer().getName() : "null"));
        System.out.println("-------------------");
    }

    /**
     * Handles the refresh button action
     */
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

    /**
     * Public method to refresh the table
     */
    public void refreshTable() {
        handleRefresh();
    }

    /**
     * Handles the export button action
     */
    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("Loan ID,Customer Name,Loan Amount,Interest Rate,Term,Monthly Payment,Total Payment");

                // Write data
                for (Loan loan : loans) {
                    writer.printf("%s,%s,%.2f,%.2f,%d,%.2f,%.2f%n",
                            loan.getLoanId(),
                            loan.getCustomerName(),
                            loan.getLoanAmount(),
                            loan.getInterestRate(),
                            loan.getTermMonths(),
                            loan.getMonthlyPayment(),
                            loan.getTotalPayment()
                    );
                }
                AlertUtils.showInfo("Success", "Data exported successfully!");
            } catch (Exception e) {
                AlertUtils.showError("Export Error", "Error exporting data: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the import button action
     */
    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine(); // Skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    // TODO: Implement import logic
                }
                loadData();
                AlertUtils.showInfo("Success", "Data imported successfully!");
            } catch (Exception e) {
                AlertUtils.showError("Import Error", "Error importing data: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the selected loan ID
     * @return The selected loan ID or null if nothing is selected
     */
    public String getSelectedLoanId() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        return selectedLoan != null ? selectedLoan.getLoanId() : null;
    }

    /**
     * Custom TableCell for currency formatting
     */
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

    /**
     * Sets the main controller reference
     */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}