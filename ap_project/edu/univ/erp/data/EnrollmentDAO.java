package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.*;

public class EnrollmentDAO {

    public boolean enrollStudent(Enrollment e) {
        String sql = "INSERT INTO enrollments(student_id, section_id, status) VALUES (?, ?, ?)";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, e.getStudentId());
            ps.setInt(2, e.getSectionId());
            ps.setString(3, e.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error enrolling student: " + ex.getMessage());
            return false;
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("student_id"),
                    rs.getInt("section_id"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching enrollments: " + e.getMessage());
        }

        return list;
    }

    public boolean deleteEnrollment(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
            return false;
        }
    }


    public List<Enrollment> getEnrollmentsBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("student_id"),
                    rs.getInt("section_id"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching enrollments by section: " + e.getMessage());
        }

        return list;
    }

    public Enrollment getEnrollmentById(int enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching enrollment by ID: " + e.getMessage());
        }

        return null;
    }


    public boolean removeStudentFromSection(int sectionId, int studentId) {
        String sql = "DELETE FROM enrollments WHERE section_id = ? AND student_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setInt(2, studentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error removing student from section: " + e.getMessage());
            return false;
        }
    }

    public boolean isStudentAlreadyEnrolledInSection(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If count > 0, already enrolled
            }

        } catch (SQLException e) {
            System.err.println("Error checking enrollment: " + e.getMessage());
        }
        return false;
    }

    public boolean enrollStudentInSection(int studentId, int sectionId) {


        if (isStudentAlreadyEnrolledInSection(studentId, sectionId)) {
            System.out.println("Student is already enrolled in this section.");
            return false;
        }

        String sql = "INSERT INTO enrollments(student_id, section_id, status) VALUES (?, ?, ?)";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.setString(3, "ENROLLED");  // default status

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error enrolling student in section: " + e.getMessage());
            return false;
        }
    }
    public Enrollment getEnrollmentByUserAndSection(int userId, int sectionId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching enrollment: " + e.getMessage());
        }

        return null;
    }







}