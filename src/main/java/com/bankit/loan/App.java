package com.bankit.loan;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.controller.LoanCalculatorController;
import com.bankit.loan.controller.LoanFormController;
import com.bankit.loan.controller.LoanTableController;
import com.bankit.loan.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Main Application class for the Loan Management System
 * Handles the primary stage and scene initialization
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("BankIT Loan Management System");

        try {
            // Load the main FXML using correct path
            URL fxmlUrl = getClass().getResource("/view/fxml/main.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Cannot find main.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get the main controller and initialize controllers
            MainController mainController = loader.getController();

            // Initialize child controllers using proper fx:id values from FXML
            mainController.setFormController(
                    (LoanFormController) loader.getNamespace().get("loanFormController")
            );
            mainController.setCalculatorController(
                    (LoanCalculatorController) loader.getNamespace().get("loanCalculatorController")
            );
            mainController.setTableController(
                    (LoanTableController) loader.getNamespace().get("loanTableController")
            );

            // Get child controllers from the FXML namespace
            LoanFormController formController =
                    (LoanFormController) loader.getNamespace().get("loanFormController");
            LoanCalculatorController calculatorController =
                    (LoanCalculatorController) loader.getNamespace().get("loanCalculatorController");
            LoanTableController tableController =
                    (LoanTableController) loader.getNamespace().get("loanTableController");

            // Set up controller relationships
            if (formController == null || calculatorController == null || tableController == null) {
                throw new RuntimeException("Failed to load all required controllers");
            }

            // Initialize controllers with bidirectional references
            mainController.setFormController(formController);
            mainController.setCalculatorController(calculatorController);
            mainController.setTableController(tableController);

            Scene scene = new Scene(root);

            // Add stylesheet with correct path
            URL cssUrl = getClass().getResource("/css/light-theme.css");
            if (cssUrl == null) {
                throw new IOException("Cannot find light-theme.css");
            }
            scene.getStylesheets().add(cssUrl.toExternalForm());

            // Configure stage
            primaryStage.setWidth(1024);
            primaryStage.setHeight(768);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(1000);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML/CSS: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        // Cleanup on application shutdown
        DatabaseConfig.closeAllConnections();
    }

    /**
     * Gets the primary stage of the application
     * @return The primary Stage object
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}