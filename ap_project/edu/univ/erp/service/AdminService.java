package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.auth.*;
import edu.univ.erp.access.*;

import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminService {

    private AdminDAO adminDAO = new AdminDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentService StudentService = new StudentService();
    private UserAuthDAO UserAuthDAO =   new UserAuthDAO();
    private InstructorService instructorService = new InstructorService();

    private void requireAdminPermission(AccessControl.Actions action) throws Exception {
        String role = SessionManager.getCurrentUserRole();

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new AccessException("Only ADMIN can perform this action");
        }

        AccessControl.assertAllowedWithMaintenance(AccessControl.Role.ADMIN, action);
    }

    // Admin operations
    public boolean addAdmin(Admin a) {
        return adminDAO.addAdmin(a);
    }

    public Admin getAdminById(int id) {
        return adminDAO.getAdminById(id);
    }


    // Course operations
    public boolean addCourse(Course c) {
        return courseDAO.addCourse(c);
    }

    public boolean updateCourse(Course c) {
        return courseDAO.updateCourse(c);
    }

    public boolean deleteCourse(String coursecode) {
        return courseDAO.deleteCourse(coursecode);
    }


    // Section operations
    public boolean addSection(Section s, String courseCode) {
        return sectionDAO.addSection(s, courseCode);
    }


    public boolean setInstructor(int sectionId, int instructorId) {
        return sectionDAO.assignInstructor(sectionId, instructorId);
    }

    public boolean changeCapacity(int sectionId, int newCapacity) {
        return sectionDAO.updateCapacity(sectionId, newCapacity);
    }


    public boolean addStudent(Student student, String username, String rawPassword) {
        // Convert raw password to hash using BCrypt
        String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
        return StudentService.addStudent(student, username, passwordHash);
    }

    public boolean dropStudentForcefully(int sectionId, int studentId) {
        return enrollmentDAO.removeStudentFromSection(sectionId, studentId);
    }

    // 1️⃣ Drop student from a section (unenroll only)
    public boolean dropStudentFromSection(int sectionId, int studentId) {
        return enrollmentDAO.removeStudentFromSection(sectionId, studentId);
    }

    // 2️⃣ Delete student completely from system
    public boolean deleteStudent(int userId) {
        // Delete from auth_db, cascades will remove from ERP DB
        return UserAuthDAO.deleteUser(userId);
    }


    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }


    // Instructor operation

    public boolean addInstructor(String username, String rawPassword, String department) {
        return instructorService.addInstructor(username, rawPassword, department);
    }

    public List<Instructor> getAllInstructors() {
        return instructorDAO.getAllInstructors();
    }


    public void toggleMaintenance() throws Exception {
        requireAdminPermission(AccessControl.Actions.TOGGLE_MAINTENANCE);

        boolean current = MaintenanceChecker.isMaintenanceOn();
        MaintenanceChecker.setMaintenance(!current);
    }
}
