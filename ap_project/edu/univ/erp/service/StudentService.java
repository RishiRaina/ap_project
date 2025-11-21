package edu.univ.erp.service;

import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserAuth;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentService {

    private StudentDAO studentDAO;
    private UserAuthDAO userAuthDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.userAuthDAO = new UserAuthDAO();
    }


    public boolean addStudent(Student student, String username, String passwordHash) {
        Connection authConn = null;
        Connection erpConn = null;

        try {
            authConn = edu.univ.erp.data.AuthDatabaseConnection.getConnection();
            erpConn = edu.univ.erp.data.ERPDatabaseConnection.getConnection();

            if (authConn == null || erpConn == null) return false;

            authConn.setAutoCommit(false);
            erpConn.setAutoCommit(false);

            // Insert user into auth_db
            UserAuth user = new UserAuth();
            user.setUsername(username);
            user.setRole("STUDENT");
            user.setPasswordHash(passwordHash);
            user.setStatus("ACTIVE");

            if (!userAuthDAO.addUser(user)) throw new SQLException("Failed to add user to auth_db");

            // Insert student into ERP DB
            student.setUserId(user.getUserId());
            if (!studentDAO.addStudent(student, erpConn)) throw new SQLException("Failed to add student to ERP DB");

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
}
