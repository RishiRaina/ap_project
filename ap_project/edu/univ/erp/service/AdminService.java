package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.auth.*;
import edu.univ.erp.access.*;

import java.util.List;

public class AdminService {

    private AdminDAO adminDAO = new AdminDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

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

    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }


    // Section operations
    public boolean addSection(Section s) {
        return sectionDAO.addSection(s);
    }

    public boolean setInstructor(int sectionId, int instructorId) {
        return sectionDAO.assignInstructor(sectionId, instructorId);
    }

    public boolean changeCapacity(int sectionId, int newCapacity) {
        return sectionDAO.updateCapacity(sectionId, newCapacity);
    }


    // Student operations
    public boolean addStudent(Student s) {
        return studentDAO.addStudent(s);
    }

    public boolean dropStudentForcefully(int sectionId, int studentId) {
        return enrollmentDAO.removeStudentFromSection(sectionId, studentId);
    }


    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }


    // Instructor operations
    public boolean addInstructor(Instructor i) {
        return instructorDAO.addInstructor(i);
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
