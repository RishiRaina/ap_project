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
}
