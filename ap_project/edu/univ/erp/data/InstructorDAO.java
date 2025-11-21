package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.*;

public class InstructorDAO {
    
    public boolean addInstructor(Instructor i) {
        String sql = "INSERT INTO instructors(user_id, department) VALUES (?, ?)";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i.getUserId());
            ps.setString(2, i.getDepartment());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding instructor: " + e.getMessage());
            return false;
        }
    }

    public Instructor getInstructorById(int userId) {
        String sql = "SELECT * FROM instructors WHERE user_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Instructor(
                        rs.getInt("user_id"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructor: " + e.getMessage());
        }
        return null;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT * FROM instructors";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Instructor(
                        rs.getInt("user_id"),
                        rs.getString("department")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructors: " + e.getMessage());
        }
        return list;
    }

    public List<Integer> getAllInstructorIds() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT user_id FROM instructors";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean updateInstructor(Instructor i) {
        String sql = "UPDATE instructors SET department = ? WHERE user_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, i.getDepartment());
            ps.setInt(2, i.getUserId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating instructor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteInstructor(int userId) {
        String sql = "DELETE FROM instructors WHERE user_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting instructor: " + e.getMessage());
            return false;
        }
    }
}
