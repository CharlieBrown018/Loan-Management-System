package com.bankit.loan.ui.panels;

import com.bankit.loan.model.LoanData;
import com.bankit.loan.ui.components.CustomLabel;
import com.bankit.loan.ui.components.CustomTextField;
import com.bankit.loan.util.Constants;
import com.bankit.loan.util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class FormPanel extends JPanel {
    // Customer fields
    private final CustomTextField txtCustomerId;
    private final CustomTextField txtFullName;
    private final CustomTextField txtContact;
    private final CustomTextField txtEmail;
    private final CustomTextField txtAddress;
    private final JComboBox<String> cmbAccountType;

    // Loan fields
    private final CustomTextField txtLoanId;
    private final CustomTextField txtLoanAmount;
    private final CustomTextField txtInterestRate;
    private final CustomTextField txtNumPayments;
    private final CustomTextField txtDateIssue;
    private final CustomTextField txtDateLast;

    // Officer fields
    private final CustomTextField txtOfficerId;
    private final CustomTextField txtOfficerName;
    private final CustomTextField txtOfficerEmail;
    private final CustomTextField txtOfficerContact;

    public FormPanel() {
        setLayout(new GridBagLayout());
        setBackground(Constants.FORM_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        // Initialize all text fields
        txtCustomerId = new CustomTextField();
        txtFullName = new CustomTextField(Constants.DEFAULT_NAME_TEXT);
        txtContact = new CustomTextField(Constants.DEFAULT_CONTACT_TEXT);
        txtEmail = new CustomTextField();
        txtAddress = new CustomTextField(Constants.DEFAULT_ADDRESS_TEXT);
        cmbAccountType = new JComboBox<>(Constants.ACCOUNT_TYPES);

        txtLoanId = new CustomTextField();
        txtLoanId.setEditable(false);
        txtLoanAmount = new CustomTextField();
        txtInterestRate = new CustomTextField();
        txtNumPayments = new CustomTextField();
        txtDateIssue = new CustomTextField();
        txtDateIssue.setEditable(false);
        txtDateLast = new CustomTextField();
        txtDateLast.setEditable(false);

        txtOfficerId = new CustomTextField();
        txtOfficerName = new CustomTextField(Constants.DEFAULT_NAME_TEXT);
        txtOfficerEmail = new CustomTextField();
        txtOfficerContact = new CustomTextField(Constants.DEFAULT_OFFICER_CONTACT_TEXT);

        layoutComponents();
        setupInitialData();
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        int gridy = 0;

        // Customer Information Section
        addSectionHeader("Customer Information", gbc, gridy++);
        addFormField("Customer ID", txtCustomerId, gbc, gridy++);
        addFormField("Full Name", txtFullName, gbc, gridy++);
        addFormField("Contact", txtContact, gbc, gridy++);
        addFormField("Email", txtEmail, gbc, gridy++);
        addFormField("Address", txtAddress, gbc, gridy++);
        addFormField("Account Type", cmbAccountType, gbc, gridy++);

        // Loan Information Section
        addSectionHeader("Loan Information", gbc, gridy++);
        addFormField("Loan ID", txtLoanId, gbc, gridy++);
        addFormField("Loan Amount", txtLoanAmount, gbc, gridy++);
        addFormField("Interest Rate", txtInterestRate, gbc, gridy++);
        addFormField("Number of Months", txtNumPayments, gbc, gridy++);
        addFormField("Date of Issue", txtDateIssue, gbc, gridy++);
        addFormField("Last Due Date", txtDateLast, gbc, gridy++);

        // Officer Information Section
        addSectionHeader("Officer Information", gbc, gridy++);
        addFormField("Officer ID", txtOfficerId, gbc, gridy++);
        addFormField("Officer Name", txtOfficerName, gbc, gridy++);
        addFormField("Officer Email", txtOfficerEmail, gbc, gridy++);
        addFormField("Officer Contact", txtOfficerContact, gbc, gridy);

        // Add note at bottom
        addNote(gbc, gridy + 1);
    }

    private void addSectionHeader(String text, GridBagConstraints gbc, int gridy) {
        gbc.gridwidth = 2;
        gbc.gridy = gridy;
        gbc.gridx = 0;
        JLabel header = new JLabel(text);
        header.setFont(new Font("Tahoma", Font.BOLD, 16));
        header.setForeground(Constants.TEXT_COLOR);
        add(header, gbc);
        gbc.gridwidth = 1;
    }

    private void addFormField(String labelText, JComponent component, GridBagConstraints gbc, int gridy) {
        CustomLabel label = new CustomLabel(labelText);
        gbc.gridy = gridy;
        gbc.gridx = 0;
        add(label, gbc);

        gbc.gridx = 1;
        add(component, gbc);
    }

    private void addNote(GridBagConstraints gbc, int gridy) {
        gbc.gridwidth = 2;
        gbc.gridy = gridy;
        gbc.gridx = 0;
        JLabel note = new JLabel("NOTE: Make sure to enter all empty and enterable fields following the format.");
        note.setFont(new Font("Tahoma", Font.PLAIN, 14));
        note.setForeground(Constants.TEXT_COLOR);
        add(note, gbc);
    }

    private void setupInitialData() {
        txtDateIssue.setText(UIUtils.getCurrentDate());
    }

    public LoanData getLoanData() {
        LoanData data = new LoanData();
        // Customer data
        data.setCustomerId(txtCustomerId.getText());
        data.setCustomerName(txtFullName.getText());
        data.setContact(txtContact.getText());
        data.setEmail(txtEmail.getText());
        data.setAddress(txtAddress.getText());
        data.setAccountType((String) cmbAccountType.getSelectedItem());

        // Loan data
        data.setLoanId(txtLoanId.getText());
        try {
            data.setLoanAmount(Double.parseDouble(txtLoanAmount.getText()));
            data.setInterestRate(Double.parseDouble(txtInterestRate.getText()));
            data.setNumberOfMonths(Integer.parseInt(txtNumPayments.getText()));
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage("Please enter valid numbers for loan amount, interest rate, and number of months");
        }
        data.setDateIssue(txtDateIssue.getText());
        data.setDateLast(txtDateLast.getText());

        // Officer data
        data.getOfficerData().setOfficerId(txtOfficerId.getText());
        data.getOfficerData().setOfficerName(txtOfficerName.getText());
        data.getOfficerData().setOfficerEmail(txtOfficerEmail.getText());
        data.getOfficerData().setOfficerContact(txtOfficerContact.getText());

        return data;
    }

    public void setLoanData(LoanData data) {
        // Customer data
        txtCustomerId.setText(data.getCustomerId());
        txtFullName.setText(data.getCustomerName());
        txtContact.setText(data.getContact());
        txtEmail.setText(data.getEmail());
        txtAddress.setText(data.getAddress());
        cmbAccountType.setSelectedItem(data.getAccountType());

        // Loan data
        txtLoanId.setText(data.getLoanId());
        txtLoanAmount.setText(String.valueOf(data.getLoanAmount()));
        txtInterestRate.setText(String.valueOf(data.getInterestRate()));
        txtNumPayments.setText(String.valueOf(data.getNumberOfMonths()));
        txtDateIssue.setText(data.getDateIssue());
        txtDateLast.setText(data.getDateLast());

        // Officer data
        txtOfficerId.setText(data.getOfficerData().getOfficerId());
        txtOfficerName.setText(data.getOfficerData().getOfficerName());
        txtOfficerEmail.setText(data.getOfficerData().getOfficerEmail());
        txtOfficerContact.setText(data.getOfficerData().getOfficerContact());
    }

    public void resetForm() {
        txtCustomerId.setText("");
        txtFullName.setText(Constants.DEFAULT_NAME_TEXT);
        txtContact.setText(Constants.DEFAULT_CONTACT_TEXT);
        txtEmail.setText("");
        txtAddress.setText(Constants.DEFAULT_ADDRESS_TEXT);
        cmbAccountType.setSelectedIndex(0);
        txtLoanId.setText("");
        txtLoanAmount.setText("");
        txtInterestRate.setText("");
        txtNumPayments.setText("");
        txtDateLast.setText("");
        txtOfficerId.setText("");
        txtOfficerName.setText(Constants.DEFAULT_NAME_TEXT);
        txtOfficerEmail.setText("");
        txtOfficerContact.setText(Constants.DEFAULT_OFFICER_CONTACT_TEXT);

        setupInitialData(); // Reset date issue to current date
    }

    // Getters for accessing form fields from outside
    public JTextField getLoanIdField() { return txtLoanId; }
    public JTextField getDateIssueField() { return txtDateIssue; }
    public JTextField getDateLastField() { return txtDateLast; }
}