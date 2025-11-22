package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class StudentQueryService {

    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private GradeDAO gradeDAO = new GradeDAO();

    public List<Course> getAllCourses() throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_CATALOG);
        return courseDAO.getAllCourses();
    }

    public List<Enrollment> getMyEnrollments(int studentId) throws AccessException{
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_TIMETABLE);
        if (studentId != SessionManager.getCurrentUserId()) {
            throw new AccessException("You may only view your own enrollments.");
        }
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public Section getSection(int sectionId) throws AccessException {
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_TIMETABLE);
        return sectionDAO.getSectionById(sectionId);
    }

    public Course getCourseById(int courseId) throws AccessException{
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_TIMETABLE);
        return courseDAO.getCourseById(courseId);
    }
    public String getFinalLetter(int enrollmentId) throws AccessException {
        List<Grade> grades = gradeDAO.getGradesByEnrollment(enrollmentId);
        for (Grade g : grades) {
            if ("FINAL".equalsIgnoreCase(g.getComponent())) {
                return (g.getFinalGrade() != null) ? g.getFinalGrade() : "N/A";
            }
        }
        return "N/A";
    }


    public List<Enrollment> getEnrollmentsBySection(int sectionId) throws AccessException{
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_TIMETABLE);
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public List<Grade> getGradesForEnrollment(int enrollmentId) throws AccessException{
        AccessControl.assertAllowed(AccessControl.Role.STUDENT, AccessControl.Actions.VIEW_GRADES);
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }

}
