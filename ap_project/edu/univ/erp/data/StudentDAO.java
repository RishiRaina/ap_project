package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import java.sql.*;
import java.util.*;

public class StudentDAO {


    public boolean addStudent(Student s, Connection conn) throws SQLException {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getProgram());
            ps.setInt(4, s.getYear());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("user_id"),
                        rs.getString("roll_no"),
                        rs.getString("program"),
                        rs.getInt("year")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }

        return list;
    }

    public Student getStudentById(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("user_id"),
                        rs.getString("roll_no"),
                        rs.getString("program"),
                        rs.getInt("year")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student: " + e.getMessage());
        }

        return null;
    }
}
