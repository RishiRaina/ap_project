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
}
