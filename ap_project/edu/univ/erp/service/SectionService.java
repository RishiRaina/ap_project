package edu.univ.erp.service;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;

import java.util.List;

public class SectionService {

    private SectionDAO sectionDAO;

    public SectionService() {
        this.sectionDAO = new SectionDAO();
    }

    public List<Section> getSectionsByCourse(int Courseid){
        return sectionDAO.getSectionsByCourse(Courseid);
    }

    public boolean addSection(Section section, String courseCode) {
        return sectionDAO.addSection(section, courseCode);
    }

    public List<Section> getAllSections() {
        return sectionDAO.getAllSections();
    }

    public Section getSectionById(int sectionId) {
        return sectionDAO.getSectionById(sectionId);
    }

    public Section getSectionByString(String text) {
        if (text == null || text.trim().isEmpty()) return null;

        try {

            String idPart = text.split("\\|")[0].trim();
            int sectionId = Integer.parseInt(idPart);
            return getSectionById(sectionId);
        } catch (Exception e) {
            System.err.println("Error parsing section string: " + text + " -> " + e.getMessage());
            return null;
        }
    }

}
