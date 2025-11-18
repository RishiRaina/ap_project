package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class InstructorQueryService {

    private SectionDAO sectionDAO = new SectionDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private GradeDAO gradeDAO = new GradeDAO();

    // Get all sections taught by instructor
    public List<Section> getMySections(int instructorId) {
        return sectionDAO.getSectionsByInstructor(instructorId);
    }

    // Get enrollments (students) in a section
    public List<Enrollment> getSectionEnrollments(int sectionId) {
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    // Get course info for a section
    public Course getCourseForSection(int sectionId) {
        Section sec = sectionDAO.getSectionById(sectionId);
        return (sec != null) ? courseDAO.getCourseById(sec.getCourseId()) : null;
    }

    // get student for an enrollment in class
    public Student getStudentForEnrollment(int enrollmentId) {
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) return null;
        return studentDAO.getStudentById(e.getStudentId());
    }

    // Get grades for an enrollment (all components)
    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }

    // Get section object
    public Section getSection(int sectionId) {
        return sectionDAO.getSectionById(sectionId);
    }
}
