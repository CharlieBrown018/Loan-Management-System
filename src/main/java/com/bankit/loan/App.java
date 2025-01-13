package com.bankit.loan;

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
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML/CSS: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Gets the primary stage of the application
     * @return The primary Stage object
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}