package edu.univ.erp.domain;

public class Student {
    private int userId;
    private String rollNo;
    private String program;
    private int year;
    private String email;

    public Student(int userid ,String rollno, String program, int year, String email){
        this.userId = userid;
        this.rollNo = rollno;
        this.program = program;
        this.year = year;
        this.email = email;
    }

    public int getStudentId() { return userId; }
    public String getEmail() { return email; }    
    public void setStudentId(int userId) { this.userId = userId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}
