package com.crs.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ActionPlan implements Serializable {
    private int id;
    private Integer milestoneId; // Nullable for standalone action plans
    private int studentId;
    private String courseCode;
    private String task;
    private Timestamp deadline;
    private String status;
    private String grade;
    private Double gradePoint;
    private String progressNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ActionPlan() {
    }

    public ActionPlan(int id, Integer milestoneId, int studentId, String courseCode, String task,
                     Timestamp deadline, String status, String grade, Double gradePoint, String progressNotes) {
        this.id = id;
        this.milestoneId = milestoneId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.task = task;
        this.deadline = deadline;
        this.status = status;
        this.grade = grade;
        this.gradePoint = gradePoint;
        this.progressNotes = progressNotes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(Integer milestoneId) {
        this.milestoneId = milestoneId;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(Double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getProgressNotes() {
        return progressNotes;
    }

    public void setProgressNotes(String progressNotes) {
        this.progressNotes = progressNotes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}