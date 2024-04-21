package com.swifttask;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.sql.*;

public class AnalyticsController {
    @FXML
    private PieChart pieChart;

    @FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private LineChart<String, Integer> lineChart;

    @FXML
    private Label tasksDoneLabel;

    @FXML
    private Label tasksOverdueLabel;

    @FXML
    private Label tasksInProgressLabel;

    private ObservableList<Task> taskList;

    public void initialize() {
        // Initialize taskList with your task data
        taskList = fetchTaskData();

        // Update charts and labels
        updateChartsAndLabels();
    }

    private ObservableList<Task> fetchTaskData() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT task, status, deadline FROM tasks");

            while (resultSet.next()) {
                String taskName = resultSet.getString("task");
                String status = resultSet.getString("status");
                Date deadline = resultSet.getDate("deadline");

                Task task = new Task(taskName, status, deadline.toLocalDate());
                taskList.add(task);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taskList;
    }

    private void updateChartsAndLabels() {
        // Calculate task statuses
        int doneCount = 0;
        int overdueCount = 0;
        int inProgressCount = 0;

        for (Task task : taskList) {
            if (task.getStatus().equalsIgnoreCase("done")) {
                doneCount++;
            } else if (task.getStatus().equalsIgnoreCase("overdue")) {
                overdueCount++;
            } else if (task.getStatus().equalsIgnoreCase("in progress")) {
                inProgressCount++;
            }
        }

        // Update PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Done", doneCount),
                new PieChart.Data("Overdue", overdueCount),
                new PieChart.Data("In Progress", inProgressCount)
        );
        pieChart.setData(pieChartData);

        // Update BarChart
        XYChart.Series<String, Integer> barChartData = new XYChart.Series<>();
        barChartData.getData().add(new XYChart.Data<>("Done", doneCount));
        barChartData.getData().add(new XYChart.Data<>("Overdue", overdueCount));
        barChartData.getData().add(new XYChart.Data<>("In Progress", inProgressCount));
        barChart.getData().add(barChartData);

        // Update LineChart (if applicable)
        // Example:
        XYChart.Series<String, Integer> lineChartData = new XYChart.Series<>();
        lineChartData.getData().add(new XYChart.Data<>("Done", doneCount));
        lineChartData.getData().add(new XYChart.Data<>("Overdue", overdueCount));
        lineChartData.getData().add(new XYChart.Data<>("In Progress", inProgressCount));
        lineChart.getData().add(lineChartData);

        // Update labels
        tasksDoneLabel.setText("Done Tasks: " + doneCount);
        tasksOverdueLabel.setText("Overdue Tasks: " + overdueCount);
        tasksInProgressLabel.setText("In Progress Tasks: " + inProgressCount);
    }
}

