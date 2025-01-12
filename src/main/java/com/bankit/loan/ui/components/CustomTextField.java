package com.bankit.loan.ui.components;

import com.bankit.loan.util.Constants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CustomTextField extends JTextField {
    public CustomTextField() {
        this(null);
    }

    public CustomTextField(String placeholder) {
        super();
        setupField(placeholder);
    }

    private void setupField(String placeholder) {
        setFont(Constants.INPUT_FONT);
        setBorder(new LineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(236, 21));
        if (placeholder != null) {
            setText(placeholder);
        }
    }
}