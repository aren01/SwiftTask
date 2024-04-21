package com.swifttask;

import java.sql.Date;
import java.time.LocalDate;

public class Task {

//    private int id;
    private LocalDate deadline;
    private String task;
    private String status;

    public Task(String task, String status, LocalDate deadline) {
//        this.id = id;
        this.task = task;
        this.status = status;
        this.deadline = deadline;
    }

    // Getters and setters
//    public int getId(){ return id; }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}

