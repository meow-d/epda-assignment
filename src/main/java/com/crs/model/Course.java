package com.crs.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String code;
    private String title;
    private int creditHours;

    public Course() {
    }

    public Course(String code, String title, int creditHours) {
        this.code = code;
        this.title = title;
        this.creditHours = creditHours;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }
}
