package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.domain.Enrollment;
import java.util.List;

public class InstructorService {
    private EnrollmentDAO enrollmentDAO;

    public InstructorService() {
        enrollmentDAO = new EnrollmentDAO();
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        
        return enrollmentDAO.getEnrollmentsByStudent(sectionId);
    }

    public boolean enterGrade(int enrollmentId, String status) {
        
        return true;
    }
}
