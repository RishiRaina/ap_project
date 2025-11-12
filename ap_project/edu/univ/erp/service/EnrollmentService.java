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
}
