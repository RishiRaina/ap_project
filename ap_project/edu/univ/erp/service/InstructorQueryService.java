package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
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
    public List<Section> getMySections(int instructorId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
        return sectionDAO.getSectionsByInstructor(instructorId);
    }

    // Get enrollments in a section
    public List<Enrollment> getSectionEnrollments(int sectionId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    // Course for section
    public Course getCourseForSection(int sectionId) throws AccessException {
        AccessControl.assertAllowed(
                AccessControl.Role.INSTRUCTOR,
                AccessControl.Actions.VIEW_SECTIONS
        );
        Section sec = sectionDAO.getSectionById(sectionId);
        return (sec != null) ? courseDAO.getCourseById(sec.getCourseId()) : null;
    }

    // Student for enrollment
    public Student getStudentForEnrollment(int enrollmentId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) return null;
        return studentDAO.getStudentById(e.getStudentId());
    }

    // get grades for enrollment
    public List<Grade> getGradesForEnrollment(int enrollmentId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }

    // get section object
    public Section getSection(int sectionId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
        return sectionDAO.getSectionById(sectionId);
    }
}
