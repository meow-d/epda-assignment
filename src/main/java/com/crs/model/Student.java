package com.crs.model;

import java.io.Serializable;

public class Student implements Serializable {
    private int id;
    private String name;
    private String program;
    private String email;
    private double currentCgpa;

    public Student() {
    }

    public Student(int id, String name, String program, String email, double currentCgpa) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.email = email;
        this.currentCgpa = currentCgpa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCurrentCgpa() {
        return currentCgpa;
    }

    public void setCurrentCgpa(double currentCgpa) {
        this.currentCgpa = currentCgpa;
    }
}
