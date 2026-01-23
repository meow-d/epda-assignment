package com.crs.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Milestone implements Serializable {
    private int id;
    private int studentId;
    private String courseCode;
    private String title;
    private String description;
    private Date targetDate;
    private String status;
    private Timestamp createdAt;

    public Milestone() {
    }

    public Milestone(int id, int studentId, String courseCode, String title, String description, Date targetDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
    }

    // Getters and Setters
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}