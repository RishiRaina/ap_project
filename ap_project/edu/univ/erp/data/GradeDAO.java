package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.*;

public class GradeDAO {

    public boolean addGrade(Grade g) {
        String sql = "INSERT INTO grades(enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?)";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, g.getEnrollmentId());
            ps.setString(2, g.getComponent());
            ps.setDouble(3, g.getScore());
            ps.setString(4, g.getFinalGrade());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
            return false;
        }
    }

    public List<Grade> getGradesByEnrollment(int enrollmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Grade(
                    rs.getInt("grade_id"),
                    rs.getInt("enrollment_id"),
                    rs.getString("component"),
                    rs.getDouble("score"),
                    rs.getString("final_grade")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching grades: " + e.getMessage());
        }

        return list;
    }
}
