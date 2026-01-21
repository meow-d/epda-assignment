package com.crs.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class RecoveryPlan implements Serializable {
    private int id;
    private int studentId;
    private String courseCode;
    private String task;
    private Timestamp deadline;
    private String status;

    public RecoveryPlan() {
    }

    public RecoveryPlan(int id, int studentId, String courseCode, String task, Timestamp deadline, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.task = task;
        this.deadline = deadline;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
