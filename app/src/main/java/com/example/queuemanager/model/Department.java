package com.example.queuemanager.model;

public class Department {
    private int departmentImage;
    private int departmentId;
    private String departmentName;

    public Department(int departmentImage, int departmentId, String departmentName) {
        this.departmentImage = departmentImage;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getDepartmentImage() {
        return departmentImage;
    }

    public void setDepartmentImage(int departmentImage) {
        this.departmentImage = departmentImage;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
