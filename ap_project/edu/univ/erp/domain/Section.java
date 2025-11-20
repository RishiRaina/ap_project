package edu.univ.erp.domain;

import java.sql.Date;

public class Section {
    private int sectionId;
    private int courseId;
    private Integer instructorId;  
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private Date registrationDeadline;

    public Section(int sectionId, int courseId, Integer instructorId, String dayTime,
                   String room, int capacity, String semester, int year, Date registrationDeadline) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.registrationDeadline = registrationDeadline;
    }

    public Section(){}

    public Section(int courseId, Integer instructorId, String dayTime, String room,
                   int capacity, String semester, int year, Date registrationDeadline) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.registrationDeadline = registrationDeadline;
    }

    public int getSectionId() { return sectionId; }
    public int getCourseId() { return courseId; }
    public Integer getInstructorId() { return instructorId; }
    public String getDayTime() { return dayTime; }
    public String getRoom() { return room; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
    public Date getRegistrationDeadline() { return registrationDeadline; }

    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }
    public void setRoom(String room) { this.room = room; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setYear(int year) { this.year = year; }
    public void setRegistrationDeadline(Date registrationDeadline) { this.registrationDeadline = registrationDeadline; }
}
