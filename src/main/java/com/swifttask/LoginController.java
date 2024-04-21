package com.swifttask;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.sql.*;

public class LoginController {
    @FXML
    private TextField nameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button loginButton;

    private Stage stage; // Injected stage

    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/swifttask";
    private static final String username = "root";
    private static final String password = "";
    private static Connection connection;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public LoginController() {
        // Default constructor
    }

    public void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String username, String password) {
        String query = "SELECT * FROM users WHERE name = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // User exists if result set is not empty
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred, treat as invalid user
        }
    }

    public void loginButtonClicked() {
        String username = nameTextField.getText();
        String password = passwordField.getText();

        if (isValidUser(username, password)) {
            openDashboard();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password. Please try again.");
            alert.showAndWait();
        }

    }
}


