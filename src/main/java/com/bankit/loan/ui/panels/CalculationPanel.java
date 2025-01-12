package com.bankit.loan.ui.panels;

import com.bankit.loan.util.Constants;
import com.bankit.loan.ui.components.CustomLabel;
import com.bankit.loan.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.border.*;

public class CalculationPanel extends JPanel {
    private JTextArea reportArea;
    private JTextField monthlyPaymentField;
    private JTextField totalPaymentField;

    public CalculationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CALC_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        // Create sub-panels
        JPanel reportPanel = createReportPanel();
        JPanel calculationResultsPanel = createCalculationResultsPanel();

        // Layout
        add(reportPanel, BorderLayout.CENTER);
        add(calculationResultsPanel, BorderLayout.SOUTH);
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.CALC_BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reportArea.setWrapStyleWord(true);
        reportArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCalculationResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.CALC_BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Monthly Payment
        CustomLabel monthlyLabel = new CustomLabel("Monthly Payment");
        monthlyLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        monthlyPaymentField = createResultField();

        // Total Payment
        CustomLabel totalLabel = new CustomLabel("Total Payment");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        totalPaymentField = createResultField();

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(monthlyLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(monthlyPaymentField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(totalLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(totalPaymentField, gbc);

        return panel;
    }

    private JTextField createResultField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setFont(new Font("Tahoma", Font.BOLD, 18));
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return field;
    }

    public void calculateLoan(double loanAmount, double interestRate, int months) {
        double payment = loanAmount + ((loanAmount * interestRate) / 100);
        double monthlyPayment = payment / months;

        monthlyPaymentField.setText(String.format("%.2f", monthlyPayment));
        totalPaymentField.setText(String.format("%.2f", payment));

        generateReport(loanAmount, interestRate, months, monthlyPayment, payment);
    }

    public boolean print(MessageFormat header, MessageFormat footer) throws PrinterException, PrinterException {
        return reportArea.print();
    }

    private void generateReport(double loanAmount, double interestRate, int months,
                                double monthlyPayment, double totalPayment) {
        StringBuilder report = new StringBuilder();
        report.append("\t\nBankIT Loan Management System Brief Statement of Loan")
                .append("\n\n-----------------------------------------------")
                .append("\nLoan Amount:\t\t Php ").append(String.format("%.2f", loanAmount))
                .append("\nInterest Rate:\t\t").append(String.format("%.2f", interestRate)).append("%")
                .append("\nNumber of Month(s):\t").append(months)
                .append("\nMonthly Payment:\t\t Php ").append(String.format("%.2f", monthlyPayment))
                .append("\nTotal Payment:\t\t Php ").append(String.format("%.2f", totalPayment))
                .append("\n-----------------------------------------------")
                .append("\n\nNOTE: Monthly payments are due on the same day each month!")
                .append("\nThank you for using BankIT Loan Management System!\n\n");

        reportArea.setText(report.toString());
    }

    public void updateReport(String reportText) {
        reportArea.setText(reportText);
    }

    public void clearReport() {
        reportArea.setText("");
        monthlyPaymentField.setText("");
        totalPaymentField.setText("");
    }

    public String getReport() {
        return reportArea.getText();
    }

    public double getMonthlyPayment() {
        try {
            return Double.parseDouble(monthlyPaymentField.getText().replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getTotalPayment() {
        try {
            return Double.parseDouble(totalPaymentField.getText().replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}