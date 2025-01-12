package com.bankit.loan.ui.panels;

import com.bankit.loan.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class HeaderPanel extends JPanel {
    private final JLabel logoLabel;
    private final JLabel titleLabel;
    private final JPanel contentPanel;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(Constants.HEADER_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BORDER_THICKNESS));

        contentPanel = new JPanel(new GridLayout(1, 2));
        contentPanel.setBackground(Constants.HEADER_BG_COLOR);

        // Initialize components
        logoLabel = createLogoLabel();
        titleLabel = createTitleLabel();

        // Layout components
        layoutComponents();
    }

    private JLabel createLogoLabel() {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon logo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Logo.png")));
            if (logo.getImageLoadStatus() == MediaTracker.COMPLETE) {
                label.setIcon(logo);
            } else {
                System.err.println("Failed to load Logo.png");
                label.setText("Logo");
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            label.setText("Logo");
        }

        return label;
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("Loan Management System");
        label.setFont(Constants.HEADER_FONT);
        label.setForeground(Constants.TEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(Constants.HEADER_BG_COLOR);
        label.setOpaque(true);
        return label;
    }

    private void layoutComponents() {
        // Add space on left and right
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Constants.HEADER_BG_COLOR);
        logoPanel.add(Box.createHorizontalStrut(25), BorderLayout.WEST);
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        contentPanel.add(logoPanel);
        contentPanel.add(titleLabel);

        // Add padding around the content
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBackground(Constants.HEADER_BG_COLOR);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        paddedPanel.add(contentPanel, BorderLayout.CENTER);

        add(paddedPanel, BorderLayout.CENTER);
    }

    // Optional: Method to update the title if needed
    public void setTitleText(String title) {
        titleLabel.setText(title);
    }
}