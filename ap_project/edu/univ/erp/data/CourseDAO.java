package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.*;

public class CourseDAO {

    public boolean addCourse(Course c) {
        String sql = "INSERT INTO courses(course_id, name, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, c.getCourseId());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCredits());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Course(
                    rs.getInt("course_id"),
                    rs.getString("name"),
                    rs.getInt("credits")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return list;
    }
}
