package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.*;

public class SectionDAO {

    public boolean addSection(Section s) {
        String sql = "INSERT INTO sections(sectionId, courseId, instructorId, dayTime, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getSectionId());
            ps.setInt(2, s.getCourseId());
            ps.setInt(3, s.getInstructorId());
            ps.setString(4, s.getDayTime());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getCapacity());
            ps.setString(7, s.getSemester());
            ps.setInt(8, s.getYear());
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
                list.add(new Section(
                    rs.getInt("sectionId"),
                    rs.getInt("courseId"),
                    rs.getInt("instructorId"),
                    rs.getString("dayTime"),
                    rs.getString("room"),
                    rs.getInt("capacity"),
                    rs.getString("semester"),
                    rs.getInt("year")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sections: " + e.getMessage());
        }
        return list;
    }
}
