package edu.univ.erp.domain;

public class Instructor {
    private int userId;
    private String department;

    public Instructor(int userId, String department) {
        this.userId = userId;
        this.department = department;
    }
    public Instructor(){}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
