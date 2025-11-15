package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Instructor;

import java.util.List;

public class InstructorService {

    private InstructorDAO instructorDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;

    public InstructorService() {
        instructorDAO = new InstructorDAO();
        enrollmentDAO = new EnrollmentDAO();
        gradeDAO = new GradeDAO();
    }
    
    public boolean addInstructor(int userId, String department) {
        return instructorDAO.addInstructor(new Instructor(userId, department));
    }

    public Instructor getInstructorById(int userId) {
        return instructorDAO.getInstructorById(userId);
    }

    public List<Instructor> getAllInstructors() {
        return instructorDAO.getAllInstructors();
    }

    public boolean updateInstructor(int userId, String department) {
        return instructorDAO.updateInstructor(new Instructor(userId, department));
    }

    public boolean deleteInstructor(int userId) {
        return instructorDAO.deleteInstructor(userId);
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public boolean enterGrade(int enrollmentId, String component, double score, String finalGrade) {
        Grade g = new Grade(enrollmentId, component, score, finalGrade);
        return gradeDAO.addGrade(g);
    }
}
