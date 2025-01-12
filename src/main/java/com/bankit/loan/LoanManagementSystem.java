package com.bankit.loan;

import com.bankit.loan.model.LoanData;
import com.bankit.loan.ui.panels.*;
import com.bankit.loan.util.Constants;
import com.bankit.loan.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.MessageFormat;
import java.io.IOException;

public class LoanManagementSystem extends JFrame {
    private final HeaderPanel headerPanel;
    private final FormPanel formPanel;
    private final CalculationPanel calculationPanel;
    private final ButtonPanel buttonPanel;
    private final TablePanel tablePanel;

    public LoanManagementSystem() {
        setTitle("BankIT Loan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(Constants.MAIN_WINDOW_SIZE);

        // Initialize panels
        headerPanel = new HeaderPanel();
        formPanel = new FormPanel();
        calculationPanel = new CalculationPanel();
        buttonPanel = new ButtonPanel();
        tablePanel = new TablePanel();

        layoutComponents();
        setupEventHandlers();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        // Center panel for form, calculation, and buttons
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(formPanel, BorderLayout.WEST);

        // Right side panel for calculation and buttons
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(calculationPanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.EAST);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        // Main content panel that will hold everything
        JPanel mainContent = new JPanel(new BorderLayout(5, 5));
        mainContent.add(headerPanel, BorderLayout.NORTH);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        // Add table panel with constraints
        mainContent.add(tablePanel, BorderLayout.SOUTH);

        // Add main content to frame
        add(mainContent);
    }

    private void setupEventHandlers() {
        // Calculate button
        buttonPanel.getCalculateButton().addActionListener(e -> performCalculation());

        // Add button
        buttonPanel.getAddButton().addActionListener(e -> addLoanRecord());

        // Update button
        buttonPanel.getUpdateButton().addActionListener(e -> updateLoanRecord());

        // Delete button
        buttonPanel.getDeleteButton().addActionListener(e -> deleteLoanRecord());

        // Reset button
        buttonPanel.getResetButton().addActionListener(e -> resetForm());

        // Generate Loan ID button
        buttonPanel.getGenerateLoanIdButton().addActionListener(e -> generateLoanId());

        // Print buttons
        buttonPanel.getPrintStatementButton().addActionListener(e -> printStatement());
        buttonPanel.getPrintReportButton().addActionListener(e -> printReport());

        // Export/Import buttons
        buttonPanel.getExportButton().addActionListener(e -> exportData());
        buttonPanel.getImportButton().addActionListener(e -> importData());

        // Exit button
        buttonPanel.getExitButton().addActionListener(e -> exitApplication());
    }

    private void performCalculation() {
        try {
            LoanData data = formPanel.getLoanData();
            calculationPanel.calculateLoan(
                    data.getLoanAmount(),
                    data.getInterestRate(),
                    data.getNumberOfMonths()
            );
        } catch (NumberFormatException e) {
            buttonPanel.showError("Please enter valid numeric values for loan calculation");
        }
    }

    private void addLoanRecord() {
        try {
            LoanData data = formPanel.getLoanData();
            data.setMonthlyPayment(calculationPanel.getMonthlyPayment());
            data.setTotalPayment(calculationPanel.getTotalPayment());
            tablePanel.addLoanData(data);
            buttonPanel.clearError();
        } catch (Exception e) {
            buttonPanel.showError("Error adding record: " + e.getMessage());
        }
    }

    private void updateLoanRecord() {
        try {
            LoanData data = formPanel.getLoanData();
            data.setMonthlyPayment(calculationPanel.getMonthlyPayment());
            data.setTotalPayment(calculationPanel.getTotalPayment());
            tablePanel.updateSelectedRow(data);
        } catch (Exception e) {
            buttonPanel.showError("Error updating record: " + e.getMessage());
        }
    }

    private void deleteLoanRecord() {
        if (UIUtils.showConfirmDialog("Are you sure you want to delete this record?", "Delete Record")) {
            tablePanel.deleteSelectedRow();
        }
    }

    private void resetForm() {
        if (UIUtils.showConfirmDialog("Reset all fields?", "Reset Confirmation")) {
            formPanel.resetForm();
            calculationPanel.clearReport();
            buttonPanel.clearError();
        }
    }

    private void generateLoanId() {
        formPanel.getLoanIdField().setText(UIUtils.generateLoanId());
    }

    private void printStatement() {
        try {
            MessageFormat header = buttonPanel.isHeaderEnabled() ?
                    new MessageFormat(buttonPanel.getHeaderText()) : null;
            MessageFormat footer = buttonPanel.isFooterEnabled() ?
                    new MessageFormat(buttonPanel.getFooterText()) : null;

            boolean complete = calculationPanel.getReport().isEmpty() ?
                    false :
                    calculationPanel.print(header, footer);

            if (complete) {
                UIUtils.showInfoMessage("Printing completed successfully");
            } else {
                UIUtils.showInfoMessage("Printing cancelled");
            }
        } catch (PrinterException e) {
            buttonPanel.showError("Printing error: " + e.getMessage());
        }
    }

    private void printReport() {
        try {
            MessageFormat header = buttonPanel.isHeaderEnabled() ?
                    new MessageFormat(buttonPanel.getHeaderText()) : null;
            MessageFormat footer = buttonPanel.isFooterEnabled() ?
                    new MessageFormat(buttonPanel.getFooterText()) : null;

            boolean complete = tablePanel.getTable().print(
                    buttonPanel.isFitWidth() ? JTable.PrintMode.FIT_WIDTH : JTable.PrintMode.NORMAL,
                    header, footer, buttonPanel.isShowPrintDialog(),
                    null, buttonPanel.isInteractive(), null);

            if (complete) {
                UIUtils.showInfoMessage("Printing completed successfully");
            } else {
                UIUtils.showInfoMessage("Printing cancelled");
            }
        } catch (PrinterException e) {
            buttonPanel.showError("Printing error: " + e.getMessage());
        }
    }

    private void exportData() {
        File dataDir = new File("src/main/resources/data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, "Entry_Data.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            tablePanel.exportToFile(file.getPath());
            UIUtils.showInfoMessage("Data exported successfully");
        } catch (IOException e) {
            buttonPanel.showError("Error creating file: " + e.getMessage());
        }
    }

    private void importData() {
        File file = new File("src/main/resources/data/Entry_Data.txt");
        if (!file.exists()) {
            UIUtils.showInfoMessage("No saved data found");
            return;
        }
        tablePanel.importFromFile(file.getPath());
        UIUtils.showInfoMessage("Data imported successfully");
    }

    private void exitApplication() {
        if (UIUtils.showConfirmDialog("Are you sure you want to exit?", "Exit Application")) {
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoanManagementSystem().setVisible(true);
        });
    }
}