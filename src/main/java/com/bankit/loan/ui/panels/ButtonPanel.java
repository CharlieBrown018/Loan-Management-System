package com.bankit.loan.ui.panels;

import com.bankit.loan.util.Constants;
import com.bankit.loan.util.UIUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.function.Consumer;

public class ButtonPanel extends JPanel {
    // Main action buttons
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton calculateButton;
    private final JButton resetButton;
    private final JButton generateLoanIdButton;

    // Print buttons
    private final JButton printStatementButton;
    private final JButton printReportButton;
    private final JButton exportButton;
    private final JButton importButton;
    private final JButton exitButton;

    // Print options
    private final JCheckBox headerBox;
    private final JCheckBox footerBox;
    private final JCheckBox showPrintDialogBox;
    private final JCheckBox interactiveBox;
    private final JCheckBox fitWidthBox;
    private final JTextField headerField;
    private final JTextField footerField;
    private final JLabel errorLabel;

    public ButtonPanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Constants.BUTTON_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        // Initialize buttons
        addButton = createButton("Add", 24);
        updateButton = createButton("Update", 24);
        deleteButton = createButton("Delete", 24);
        calculateButton = createButton("Calculate", 24);
        resetButton = createButton("Reset", 24);
        generateLoanIdButton = createButton("Generate Loan ID", 20);
        printStatementButton = createButton("Print Brief Statement", 18);
        printReportButton = createButton("Print Tabular Report", 20);
        exportButton = createButton("Export", 18);
        importButton = createButton("Import", 18);
        exitButton = createButton("Exit", 24);

        // Initialize print options
        headerBox = new JCheckBox("Header:");
        footerBox = new JCheckBox("Footer:");
        showPrintDialogBox = new JCheckBox("Show print dialog");
        interactiveBox = new JCheckBox("Interactive (Show status dialog)");
        fitWidthBox = new JCheckBox("Fit width");
        headerField = new JTextField("Insert Header");
        footerField = new JTextField("Page {0}");
        errorLabel = new JLabel();

        setupComponents();
        layoutComponents();
    }

    private JButton createButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        button.setBackground(Constants.TEXT_COLOR);
        button.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        return button;
    }

    private void setupComponents() {
        headerBox.addActionListener(e -> headerField.setEnabled(headerBox.isSelected()));
        footerBox.addActionListener(e -> footerField.setEnabled(footerBox.isSelected()));

        showPrintDialogBox.addActionListener(e -> {
            if (!showPrintDialogBox.isSelected()) {
                UIUtils.showInfoMessage("If the Print Dialog is not shown, the default printer is used.");
            }
        });

        interactiveBox.addActionListener(e -> {
            if (!interactiveBox.isSelected()) {
                UIUtils.showInfoMessage("If non-interactive, the GUI is fully blocked during printing.");
            }
        });

        errorLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                "Error Notifier",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 12)
        ));
        errorLabel.setOpaque(true);
        errorLabel.setBackground(Constants.TEXT_COLOR);
    }

    private void layoutComponents() {
        // Main buttons panel
        JPanel mainButtonsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        mainButtonsPanel.setBackground(Constants.BUTTON_BG_COLOR);
        mainButtonsPanel.add(addButton);
        mainButtonsPanel.add(printStatementButton);
        mainButtonsPanel.add(updateButton);
        mainButtonsPanel.add(printReportButton);
        mainButtonsPanel.add(deleteButton);
        mainButtonsPanel.add(exitButton);
        mainButtonsPanel.add(resetButton);
        mainButtonsPanel.add(generateLoanIdButton);
        mainButtonsPanel.add(calculateButton);

        // Print options panel
        JPanel printOptionsPanel = createPrintOptionsPanel();

        // Export/Import panel
        JPanel exportImportPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        exportImportPanel.setBackground(Constants.BUTTON_BG_COLOR);
        exportImportPanel.add(exportButton);
        exportImportPanel.add(importButton);

        // Main layout
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Constants.BUTTON_BG_COLOR);
        centerPanel.add(mainButtonsPanel, BorderLayout.NORTH);
        centerPanel.add(printOptionsPanel, BorderLayout.CENTER);
        centerPanel.add(exportImportPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
        add(errorLabel, BorderLayout.SOUTH);
    }

    private JPanel createPrintOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Constants.TEXT_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                "Printing",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 12)
        ));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        // Add header options
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(headerBox, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(headerField, gbc);

        // Add footer options
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(footerBox, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(footerField, gbc);

        // Add checkboxes
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(showPrintDialogBox, gbc);

        gbc.gridy = 3;
        panel.add(interactiveBox, gbc);

        gbc.gridy = 4;
        panel.add(fitWidthBox, gbc);

        return panel;
    }

    // Getter methods for components
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getCalculateButton() { return calculateButton; }
    public JButton getResetButton() { return resetButton; }
    public JButton getGenerateLoanIdButton() { return generateLoanIdButton; }
    public JButton getPrintStatementButton() { return printStatementButton; }
    public JButton getPrintReportButton() { return printReportButton; }
    public JButton getExportButton() { return exportButton; }
    public JButton getImportButton() { return importButton; }
    public JButton getExitButton() { return exitButton; }

    // Print settings getters
    public boolean isHeaderEnabled() { return headerBox.isSelected(); }
    public boolean isFooterEnabled() { return footerBox.isSelected(); }
    public boolean isShowPrintDialog() { return showPrintDialogBox.isSelected(); }
    public boolean isInteractive() { return interactiveBox.isSelected(); }
    public boolean isFitWidth() { return fitWidthBox.isSelected(); }
    public String getHeaderText() { return headerField.getText(); }
    public String getFooterText() { return footerField.getText(); }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }
}