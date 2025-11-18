package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.*;

public class SectionDAO {

    public boolean addSection(Section s) {
        String sql = "INSERT INTO sections(course_id, instructor_id, day_time, room, capacity, semester, year, registration_deadline) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseId());
            if (s.getInstructorId() == null) {
                ps.setNull(2, Types.INTEGER);
            }
            else {
                ps.setInt(2, s.getInstructorId());
            }
            ps.setString(3, s.getDayTime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setString(6, s.getSemester());
            ps.setInt(7, s.getYear());
            ps.setDate(8, s.getRegistrationDeadline());
            return ps.executeUpdate() > 0;

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


}
