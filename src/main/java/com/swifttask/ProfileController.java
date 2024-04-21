package com.swifttask;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileController {

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    public void initialize() {
        // Load database driver and establish connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");

            // Prepare and execute SQL query
            String query = "SELECT id, name, phone FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getUserId()); // Assuming getUserId() retrieves the current user's ID
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");

                // Set retrieved data to Labels
                idLabel.setText("ID: " + id);
                nameLabel.setText("Name: " + name);
                phoneLabel.setText("Mobile Number: " + phone);
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }
    private int getUserId(){
        return 1;
    }

}
