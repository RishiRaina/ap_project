package edu.univ.erp.service;


import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.sql.Connection;
import java.sql.SQLException;


import java.util.List;

public class InstructorService {

    private InstructorDAO instructorDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;
    private UserAuthDAO userAuthDAO;

    public InstructorService() {
        instructorDAO = new InstructorDAO();
        enrollmentDAO = new EnrollmentDAO();
        gradeDAO = new GradeDAO();
        userAuthDAO = new UserAuthDAO();
    }

    public boolean addInstructor(String username, String rawPassword, String department) {
        Connection authConn = null;
        Connection erpConn = null;

        try {
            authConn = edu.univ.erp.data.AuthDatabaseConnection.getConnection();
            erpConn = edu.univ.erp.data.ERPDatabaseConnection.getConnection();

            if (authConn == null || erpConn == null) return false;

            authConn.setAutoCommit(false);
            erpConn.setAutoCommit(false);

            // 1. Hash password
            String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

            // 2. Add to auth_db
            UserAuth user = new UserAuth();
            user.setUsername(username);
            user.setPasswordHash(passwordHash);
            user.setRole("INSTRUCTOR");
            user.setStatus("ACTIVE");

            if (!userAuthDAO.addUser(user)) throw new SQLException("Failed to add user to auth_db");

            // 3. Add to instructors table in erp_db
            Instructor inst = new Instructor();
            inst.setUserId(user.getUserId());  // Use user_id from auth_db
            inst.setDepartment(department);

            if (!instructorDAO.addInstructor(inst)) throw new SQLException("Failed to add instructor to ERP DB");

            authConn.commit();
            erpConn.commit();
            return true;

        } catch (Exception e) {
            try { if (authConn != null) authConn.rollback(); } catch (SQLException ignored) {}
            try { if (erpConn != null) erpConn.rollback(); } catch (SQLException ignored) {}
            return false;

        } finally {
            try { if (authConn != null) authConn.setAutoCommit(true); } catch (SQLException ignored) {}
            try { if (erpConn != null) erpConn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
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
