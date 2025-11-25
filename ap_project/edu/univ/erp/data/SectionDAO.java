package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.*;

public class SectionDAO {

    public boolean addSection(Section s, String courseCode) {

        String findCourseSql = "SELECT course_id FROM courses WHERE code = ?";
        String insertSql = "INSERT INTO sections(course_id, instructor_id, day_time, room, capacity, semester, year, registration_deadline) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ERPDatabaseConnection.getConnection()) {

            // 1. Fetch course_id using course_code
            int courseId = -1;
            try (PreparedStatement ps = conn.prepareStatement(findCourseSql)) {
                ps.setString(1, courseCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("course_id");
                } else {
                    System.err.println("Course with code " + courseCode + " not found!");
                    return false;
                }
            }

            // 2. Insert the new section
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

                ps.setInt(1, courseId);

                if (s.getInstructorId() == null) ps.setNull(2, Types.INTEGER);
                else ps.setInt(2, s.getInstructorId());

                ps.setString(3, s.getDayTime());
                ps.setString(4, s.getRoom());
                ps.setInt(5, s.getCapacity());
                ps.setString(6, s.getSemester());
                ps.setInt(7, s.getYear());
                ps.setDate(8, s.getRegistrationDeadline());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error adding section: " + e.getMessage());
            return false;
        }
    }


    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Section(rs.getInt("section_id"), rs.getInt("course_id"), rs.getObject("instructor_id") == null ? null : rs.getInt("instructor_id"),
                    rs.getString("day_time"), rs.getString("room"), rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year"), rs.getDate("registration_deadline")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching sections: " + e.getMessage());
        }
        return list;
    }

    public Section getSectionById(int sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Section(rs.getInt("section_id"), rs.getInt("course_id"), rs.getObject("instructor_id") == null ? null : rs.getInt("instructor_id"),
                        rs.getString("day_time"), rs.getString("room"), rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year"), rs.getDate("registration_deadline")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching section by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE instructor_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Section(rs.getInt("section_id"), rs.getInt("course_id"), rs.getObject("instructor_id") == null ? null : rs.getInt("instructor_id"),
                        rs.getString("day_time"), rs.getString("room"), rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year"),
                        rs.getDate("registration_deadline")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructor sections:" + e.getMessage());
        }

        return list;
    }

    // NEW METHOD — Get instructor's user_id for a given section
    public int getInstructorUserId(int sectionId) {
        String sql = "SELECT instructor_id FROM sections WHERE section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int val = rs.getInt("instructor_id");
                return rs.wasNull() ? -1 : val;   // return -1 if null
            }

        } catch (SQLException e) {
            System.err.println("Error getting instructor user ID: " + e.getMessage());
        }

        return -1;
    }


    public boolean assignInstructor(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error assigning instructor: " + e.getMessage());
            return false;
        }
    }


    public boolean updateCapacity(int sectionId, int newCapacity) {
        String sql = "UPDATE sections SET capacity = ? WHERE section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newCapacity);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating capacity: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting section: " + e.getMessage());
            return false;
        }
    }

    public List<Section> getSectionsByCourse(int courseId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ?";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getObject("instructor_id") == null ? null : rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getDate("registration_deadline")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



}
