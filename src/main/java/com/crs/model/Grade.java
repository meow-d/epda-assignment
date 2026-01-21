package com.crs.model;

import java.io.Serializable;

public class Grade implements Serializable {
    private int studentId;
    private String courseCode;
    private String semester;
    private int year;
    private int attemptNo;
    private String grade;
    private double gradePoint;
    private String status;

    public Grade() {
    }

    public Grade(int studentId, String courseCode, String semester, int year, int attemptNo, String grade, double gradePoint, String status) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.year = year;
        this.attemptNo = attemptNo;
        this.grade = grade;
        this.gradePoint = gradePoint;
        this.status = status;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        this.attemptNo = attemptNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
