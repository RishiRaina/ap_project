package edu.univ.erp.domain;

import java.time.LocalDate;

public class Section {
    private int sectionId;
    private int courseId;
    private int instructorId; 
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private LocalDate registrationDeadline;

    public Section(int sectionid,int courseid,int instructorid,String daytime,String room,int capacity,String semester,int year,LocalDate registrationdeadline){
        this.sectionId = sectionid;
        this.courseId = courseid;
        this.instructorId = instructorid;
        this.dayTime =daytime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.registrationDeadline =registrationdeadline;


    }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public String getDayTime() { return dayTime; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; }
}

