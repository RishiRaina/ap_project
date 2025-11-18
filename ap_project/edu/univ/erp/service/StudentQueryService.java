package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class StudentQueryService {

    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private GradeDAO gradeDAO = new GradeDAO();

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public List<Enrollment> getMyEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public Section getSection(int sectionId) {
        return sectionDAO.getSectionById(sectionId);
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getCourseById(courseId);
    }

    public List<Enrollment> getEnrollmentsBySection(int sectionId) {
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }

}
