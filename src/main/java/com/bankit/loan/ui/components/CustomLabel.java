package com.bankit.loan.ui.components;

import com.bankit.loan.util.Constants;

import javax.swing.*;

public class CustomLabel extends JLabel {
    public CustomLabel(String text) {
        super(text);
        setFont(Constants.LABEL_FONT);
        setForeground(Constants.TEXT_COLOR);
    }
}