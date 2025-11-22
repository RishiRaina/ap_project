package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Enrollment;
import java.util.List;

public class EnrollmentService {
    private EnrollmentDAO enrollmentDAO;

    public EnrollmentService() {
        enrollmentDAO = new EnrollmentDAO();
    }

    public boolean enrollStudent(Enrollment e) {
        return enrollmentDAO.enrollStudent(e);
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public boolean deleteEnrollment(int enrollmentId) {
        return enrollmentDAO.deleteEnrollment(enrollmentId);
    }

    public List<Enrollment> getEnrollmentsBySection(int sectionId) {
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public boolean enrollStudentInSection(int studentId, int sectionId) {

        // Basic validation
        if (studentId <= 0 || sectionId <= 0) {
            System.err.println("Invalid student or section ID.");
            return false;
        }

        // Call DAO method
        boolean result = enrollmentDAO.enrollStudentInSection(studentId, sectionId);

        if (!result) {
            System.out.println("Enrollment failed. Student may already be enrolled or DB error occurred.");
        }

        return result;
    }
}
