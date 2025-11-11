package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.*;

public class InstructorDAO {

    public boolean addInstructor(Instructor i) {
        String sql = "INSERT INTO instructors(instructor_id, name, email, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i.getInstructorId());
            ps.setString(2, i.getName());
            ps.setString(3, i.getEmail());
            ps.setString(4, i.getDepartment());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding instructor: " + e.getMessage());
            return false;
        }
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT * FROM instructors";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Instructor(
                    rs.getInt("instructor_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructors: " + e.getMessage());
        }
        return list;
    }
}
