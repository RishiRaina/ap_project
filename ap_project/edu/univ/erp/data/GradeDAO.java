package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.*;

public class GradeDAO {

    public boolean addGrade(Grade g) {
        String sql = "INSERT INTO grades(student_id, course_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getCourseId());
            ps.setString(3, g.getGrade());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
            return false;
        }
    }

    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Grade(
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("grade")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching grades: " + e.getMessage());
        }
        return list;
    }
}
