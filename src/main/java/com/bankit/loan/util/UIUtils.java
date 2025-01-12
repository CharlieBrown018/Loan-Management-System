package com.bankit.loan.util;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UIUtils {
    public static void addComponentWithConstraints(Container container, Component component,
                                                   GridBagConstraints gbc, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        container.add(component, gbc);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM--dd--yyyy");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String generateLoanId() {
        int num1 = 1325 + (int) (Math.random() * 432871943);
        return String.valueOf(num1 + 1325);
    }

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirmDialog(String message, String title) {
        int result = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}