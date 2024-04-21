package com.swifttask;

import javafx.collections.transformation.FilteredList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class TaskController {

    @FXML
    private TableView<Task> taskTableView;

    @FXML
    private TableColumn<Task, String> task;

    @FXML
    private TableColumn<Task, String> status;

    @FXML
    private TableColumn<Task, LocalDate> deadline;

    @FXML
    private TextField taskField;

    @FXML
    private TextField statusField;

    @FXML
    private RadioButton tasksRadio;

    @FXML
    private RadioButton statusRadio;

    @FXML
    private RadioButton deadlineRadio;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private FilteredList<Task> filteredList;

    @FXML
    private TextField searchField;
    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    public void initialize() {
        // Initialize columns
        task.setCellValueFactory(new PropertyValueFactory<>("task"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        deadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        taskTableView.setItems(taskList);

        toggleGroup = new ToggleGroup();

        tasksRadio.setToggleGroup(toggleGroup);
        statusRadio.setToggleGroup(toggleGroup);
        deadlineRadio.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tasksRadio) {
                sortTasks(null);
            } else if (newValue == statusRadio) {
                sortStatus(null);
            } else if (newValue == deadlineRadio) {
                sortDeadline(null);
            }
        });

        tasksRadio.setSelected(true);

        sortTasks(null);
        

        // Retrieve tasks from the database and populate the TableView
        fetchTasksFromDatabase();

        taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
                taskField.setText(selectedTask.getTask());
                statusField.setText(selectedTask.getStatus());
                deadlinePicker.setValue(selectedTask.getDeadline());
            }
        });

        filteredList = new FilteredList<>(taskList);

        // Set the filter predicate to match the search query
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(task -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all tasks if search field is empty
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (task.getTask().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Match task name
                } else if (task.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Match status
                } else if (task.getDeadline() != null) {
                    // Check if deadline matches the search query
                    LocalDate deadline = task.getDeadline();
                    String deadlineStr = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Format the date
                    return deadlineStr.contains(lowerCaseFilter);
                }
                else {
                    // You can add more conditions to search other fields if needed
                    return false; // No match
                }
            });
        });

        // Bind the filtered list to the TableView
        taskTableView.setItems(filteredList);
    }

    private void fetchTasksFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT task, status, deadline FROM tasks");

            taskList.clear();

            while (resultSet.next()) {
                String taskName = resultSet.getString("task");
                String status = resultSet.getString("status");
                LocalDate deadline = resultSet.getDate("deadline").toLocalDate();

                Task task = new Task(taskName, status, deadline);
                taskList.add(task);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addTask(ActionEvent event) {
        String taskName = taskField.getText();
        String status = statusField.getText();
        LocalDate deadlineDate = deadlinePicker.getValue();

        Task newTask = new Task(taskName, status, deadlineDate);
        addTaskToDatabase(newTask);
        taskList.add(newTask);

        clearFields();
    }

    @FXML
    private void editTask(ActionEvent event) {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            String taskName = taskField.getText();
            String status = statusField.getText();
            LocalDate deadlineDate = deadlinePicker.getValue();

            selectedTask.setTask(taskName);
            selectedTask.setStatus(status);
            selectedTask.setDeadline(deadlineDate);

            updateTaskInDatabase(selectedTask);
            taskTableView.refresh();

            clearFields();
        }
    }

    @FXML
    private void deleteTask(ActionEvent event) {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            deleteTaskFromDatabase(selectedTask);
            taskList.remove(selectedTask);
        }
    }

    private void addTaskToDatabase(Task task) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tasks (task, status, deadline) VALUES (?, ?, ?)");

            statement.setString(1, task.getTask());
            statement.setString(2, task.getStatus());
            statement.setDate(3, Date.valueOf(task.getDeadline()));

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskInDatabase(Task task) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            PreparedStatement statement = connection.prepareStatement("UPDATE tasks SET task = ?, status = ?, deadline = ? WHERE task = ?");

            statement.setString(1, task.getTask());
            statement.setString(2, task.getStatus());
            statement.setDate(3, Date.valueOf(task.getDeadline()));
            statement.setString(4, task.getTask());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTaskFromDatabase(Task task) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swifttask", "root", "");
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE task = ?");
            statement.setString(1, task.getTask());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        taskField.clear();
        statusRadio.setSelected(false);
        deadlinePicker.setValue(null);
    }

    public void sortTasks(ActionEvent event) {
        taskList.sort(Comparator.comparing(Task::getTask));
        updateTable();
    }

    public void sortStatus(ActionEvent event) {
        taskList.sort(Comparator.comparing(Task::getStatus));
        updateTable();
    }

    public void sortDeadline(ActionEvent event) {
        taskList.sort(Comparator.comparing(Task::getDeadline));
        updateTable();
    }

    private void updateTable() {
        taskTableView.setItems(taskList);
    }
}
