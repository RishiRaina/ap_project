package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Enrollment;
import java.util.List;

public class StudentService {
    private EnrollmentDAO enrollmentDAO;

    public StudentService() {
        enrollmentDAO = new EnrollmentDAO();
    }

    public boolean registerSection(Enrollment e) {
       
        return enrollmentDAO.enrollStudent(e);
    }

    public boolean dropSection(int enrollmentId) {
        return enrollmentDAO.deleteEnrollment(enrollmentId);
    }

    public List<Enrollment> getMyEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }
}
