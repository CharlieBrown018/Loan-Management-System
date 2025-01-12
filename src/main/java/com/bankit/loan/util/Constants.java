package com.bankit.loan.util;

import java.awt.*;

public class Constants {
    // Colors
    public static final Color HEADER_BG_COLOR = new Color(240, 58, 71);
    public static final Color FORM_BG_COLOR = new Color(24, 48, 89);
    public static final Color CALC_BG_COLOR = new Color(39, 111, 191);
    public static final Color TEXT_COLOR = new Color(246, 244, 243);
    public static final Color BUTTON_BG_COLOR = new Color(175, 91, 91);

    // Fonts
    public static final Font HEADER_FONT = new Font("Tahoma", Font.BOLD, 36);
    public static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 14);
    public static final Font INPUT_FONT = new Font("Tahoma", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Tahoma", Font.BOLD, 24);

    // Dimensions
    public static final Dimension MAIN_WINDOW_SIZE = new Dimension(1370, 800);
    public static final Dimension FIELD_SIZE = new Dimension(236, 21);

    // Border settings
    public static final int BORDER_THICKNESS = 4;

    // Default text
    public static final String DEFAULT_NAME_TEXT = "Enter as Full_Name";
    public static final String DEFAULT_ADDRESS_TEXT = "Enter as Address_Address";
    public static final String DEFAULT_CONTACT_TEXT = "Enter 11 digit number starting 09...";
    public static final String DEFAULT_OFFICER_CONTACT_TEXT = "Enter 10 digit number starting 09...";

    // Account types
    public static final String[] ACCOUNT_TYPES = {
            "Pick an Account Type",
            "Savings",
            "Checking",
            "Joint"
    };
}