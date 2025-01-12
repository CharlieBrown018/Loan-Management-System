package com.bankit.loan.ui.panels;

import com.bankit.loan.model.LoanData;
import com.bankit.loan.util.Constants;
import com.bankit.loan.ui.components.CustomTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TablePanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private CustomTextField searchField;
    private final JLabel errorLabel;

    private static final String[] COLUMN_NAMES = {
            "Customer ID", "Full Name", "Contact", "Email", "Address", "Account Type",
            "Loan ID", "Loan Amount", "Interest Rate", "No of Payments", "Date of Loan Issue",
            "Last Due Date", "Monthly Payment", "Total Loan Payment", "Loan Officer ID",
            "Loan Officer Full Name", "Loan Officer Email", "Loan Officer Contact"
    };

    public TablePanel() {
        // Initialize the table model with column names
        this.tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        setLayout(new BorderLayout(5, 5));
        setBackground(Constants.HEADER_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        // Set preferred size for the entire panel
        setPreferredSize(new Dimension(getWidth(), 200)); // Fixed height of 200px

        // Initialize table
        table = createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(getWidth(), 150)); // Scroll pane height

        // Create search panel
        JPanel searchPanel = createSearchPanel();

        // Create error label
        errorLabel = createErrorLabel();

        // Layout components
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
        add(errorLabel, BorderLayout.NORTH);
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true); // Makes table fill the viewport

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Constants.HEADER_BG_COLOR);

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        searchLabel.setForeground(Constants.TEXT_COLOR);

        searchField = new CustomTextField("Search starting keyword or digit...");
        searchField.setPreferredSize(new Dimension(300, 25));
        searchField.addActionListener(e -> performSearch());

        panel.add(searchLabel);
        panel.add(searchField);

        return panel;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Tahoma", Font.PLAIN, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.RED);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    public void addLoanData(LoanData data) {
        tableModel.addRow(new Object[]{
                data.getCustomerId(),
                data.getCustomerName(),
                data.getContact(),
                data.getEmail(),
                data.getAddress(),
                data.getAccountType(),
                data.getLoanId(),
                String.format("%.2f", data.getLoanAmount()),
                String.format("%.2f", data.getInterestRate()),
                data.getNumberOfMonths(),
                data.getDateIssue(),
                data.getDateLast(),
                String.format("%.2f", data.getMonthlyPayment()),
                String.format("%.2f", data.getTotalPayment()),
                data.getOfficerData().getOfficerId(),
                data.getOfficerData().getOfficerName(),
                data.getOfficerData().getOfficerEmail(),
                data.getOfficerData().getOfficerContact()
        });
    }

    public void updateSelectedRow(LoanData data) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            updateRow(modelRow, data);
            clearError();
        } else {
            showError("Please select a row to update");
        }
    }

    private void updateRow(int row, LoanData data) {
        tableModel.setValueAt(data.getCustomerId(), row, 0);
        tableModel.setValueAt(data.getCustomerName(), row, 1);
        tableModel.setValueAt(data.getContact(), row, 2);
        tableModel.setValueAt(data.getEmail(), row, 3);
        tableModel.setValueAt(data.getAddress(), row, 4);
        tableModel.setValueAt(data.getAccountType(), row, 5);
        tableModel.setValueAt(data.getLoanId(), row, 6);
        tableModel.setValueAt(String.format("%.2f", data.getLoanAmount()), row, 7);
        tableModel.setValueAt(String.format("%.2f", data.getInterestRate()), row, 8);
        tableModel.setValueAt(data.getNumberOfMonths(), row, 9);
        tableModel.setValueAt(data.getDateIssue(), row, 10);
        tableModel.setValueAt(data.getDateLast(), row, 11);
        tableModel.setValueAt(String.format("%.2f", data.getMonthlyPayment()), row, 12);
        tableModel.setValueAt(String.format("%.2f", data.getTotalPayment()), row, 13);
        tableModel.setValueAt(data.getOfficerData().getOfficerId(), row, 14);
        tableModel.setValueAt(data.getOfficerData().getOfficerName(), row, 15);
        tableModel.setValueAt(data.getOfficerData().getOfficerEmail(), row, 16);
        tableModel.setValueAt(data.getOfficerData().getOfficerContact(), row, 17);
    }

    public void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            tableModel.removeRow(modelRow);
            clearError();
        } else {
            showError("Please select a row to delete");
        }
    }

    public void exportToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    writer.write(tableModel.getValueAt(i, j).toString());
                    writer.write(" ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Error exporting data: " + e.getMessage());
        }
    }

    public void importFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                tableModel.addRow(data);
            }
        } catch (IOException e) {
            showError("Error importing data: " + e.getMessage());
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        if (searchText.length() > 0) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        } else {
            sorter.setRowFilter(null);
        }
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public JTable getTable() {
        return table;
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }
}