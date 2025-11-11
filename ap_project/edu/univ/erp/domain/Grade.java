package edu.univ.erp.domain;

public class Grade {
    private int studentId;
    private int courseId;
    private String grade;

    public Grade(int studentId, int courseId, String grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }

   
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public String getGrade() { return grade; }

    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setGrade(String grade) { this.grade = grade; }
}
