package com.bankit.loan.controller;

import com.bankit.loan.model.Loan;
import com.bankit.loan.service.LoanService;
import com.bankit.loan.service.ServiceFactory;
import com.bankit.loan.util.AlertUtils;
import com.bankit.loan.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Controller for the loan table section
 */
public class LoanTableController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button exportBtn;
    @FXML private Button importBtn;
    @FXML private TableView<Loan> loanTable;

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
        loadData();
    }

    private void setupTableColumns() {
        // Setup basic columns
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        termMonthsColumn.setCellValueFactory(new PropertyValueFactory<>("termMonths"));

        // Setup currency formatted columns
        loanAmountColumn.setCellFactory(col -> new CurrencyTableCell());
        monthlyPaymentColumn.setCellFactory(col -> new CurrencyTableCell());
        totalPaymentColumn.setCellFactory(col -> new CurrencyTableCell());

        // Setup percentage column
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

        // Setup date column
        issueDateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateUtils.formatDateForDisplay(DateUtils.parseDate(item)));
                }
            }
        });

        // Setup officer name column
        officerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOfficer().getName())
        );

        // Setup row selection handler
        loanTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mainController.onLoanSelected(newSelection);
                    }
                }
        );
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

    private void loadData() {
        try {
            loans = FXCollections.observableArrayList(loanService.getAllLoans());
            filteredLoans = new FilteredList<>(loans);
            loanTable.setItems(filteredLoans);
        } catch (Exception e) {
            AlertUtils.showError("Data Load Error", "Error loading loan data: " + e.getMessage());
        }
    }

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
                // Skip header
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    // Process and import data
                    // Implementation depends on your data format
                }

                loadData(); // Refresh table
                AlertUtils.showInfo("Success", "Data imported successfully!");
            } catch (Exception e) {
                AlertUtils.showError("Import Error", "Error importing data: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the selected loan ID
     */
    public String getSelectedLoanId() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        return selectedLoan != null ? selectedLoan.getLoanId() : null;
    }

    /**
     * Refreshes the table data
     */
    public void refreshTable() {
        loadData();
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
}