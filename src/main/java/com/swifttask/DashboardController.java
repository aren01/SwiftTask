package com.swifttask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;

public class DashboardController {

    @FXML
    private Button tasksButton;

    @FXML
    private Button analyticsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button profileButton;

    @FXML
    private PieChart taskCompletionChart;

    @FXML
    private ListView<String> inProgressListView;

    @FXML
    private ListView<String> overdueListView;

    public void initialize() {
        // Initialize the ListViews with data from the database
        initializeInProgressListView();
        initializeOverdueListView();
    }

    @FXML
    private void taskButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tasklist.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tasks List");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void analyticsButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("analytics.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Analytics");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void profileButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Analytics");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void logoutButtonClicked(ActionEvent event) {
        Platform.exit();
    }

    private void initializeInProgressListView() {
        ObservableList<String> inProgressTasks = FXCollections.observableArrayList();
        inProgressListView.setItems(inProgressTasks);
        inProgressListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            PreparedStatement statement = connection.prepareStatement("SELECT task FROM tasks WHERE status = 'In Progress'");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String taskName = resultSet.getString("task");
                inProgressTasks.add(taskName);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeOverdueListView() {
        ObservableList<String> overdueTasks = FXCollections.observableArrayList();
        overdueListView.setItems(overdueTasks);
        overdueListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            PreparedStatement statement = connection.prepareStatement("SELECT task FROM tasks WHERE status = 'Overdue'");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String taskName = resultSet.getString("task");
                overdueTasks.add(taskName);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
