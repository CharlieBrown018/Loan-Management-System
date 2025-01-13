package com.bankit.loan.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


import java.util.Optional;

/**
 * Utility class for handling JavaFX alerts and dialogs
 */
public class AlertUtils {

    /**
     * Shows an error alert
     *
     * @param title the alert title
     * @param content the alert content
     */
    public static void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        configureAlert(alert, title, content);
        alert.showAndWait();
    }

    /**
     * Shows an information alert
     *
     * @param title the alert title
     * @param content the alert content
     */
    public static void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        configureAlert(alert, title, content);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert
     *
     * @param title the alert title
     * @param content the alert content
     */
    public static void showWarning(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        configureAlert(alert, title, content);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog
     *
     * @param title the dialog title
     * @param content the dialog content
     * @return true if OK was clicked, false otherwise
     */
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        configureAlert(alert, title, content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a custom confirmation dialog with specific button types
     *
     * @param title the dialog title
     * @param content the dialog content
     * @param buttonTypes the button types to display
     * @return the ButtonType that was clicked
     */
    public static ButtonType showCustomConfirmation(String title, String content, ButtonType... buttonTypes) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        configureAlert(alert, title, content);
        alert.getButtonTypes().setAll(buttonTypes);
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL);
    }

    /**
     * Configures common alert properties
     *
     * @param alert the alert to configure
     * @param title the alert title
     * @param content the alert content
     */
    private static void configureAlert(Alert alert, String title, String content) {
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        // Apply CSS to alert
        alert.getDialogPane().getStylesheets().add(
                AlertUtils.class.getResource("/css/light-theme.css").toExternalForm()
        );
    }

    /**
     * Shows an exception alert with stack trace
     *
     * @param title the alert title
     * @param e the exception to display
     */
    public static void showException(String title, Exception e) {
        StringBuilder content = new StringBuilder();
        content.append(e.getMessage()).append("\n\nStack trace:\n");

        for (StackTraceElement element : e.getStackTrace()) {
            content.append(element.toString()).append("\n");
        }

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error occurred");
        alert.setContentText(content.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    /**
     * Shows a custom input dialog
     *
     * @param title the dialog title
     * @param content the dialog content
     * @param defaultValue the default input value
     * @return the input value or empty if cancelled
     */
    public static Optional<String> showInputDialog(String title, String content, String defaultValue) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return dialog.showAndWait();
    }
}