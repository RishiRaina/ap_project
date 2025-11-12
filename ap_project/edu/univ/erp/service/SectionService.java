package edu.univ.erp.service;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;
import java.util.List;

public class SectionService {
    private SectionDAO sectionDAO = new SectionDAO();

    public boolean addSection(Section s) {
        return sectionDAO.addSection(s);
    }

    public List<Section> getAllSections() {
        return sectionDAO.getAllSections();
    }

    public List<Section> getSectionsByInstructor(int instructorId) {
        return sectionDAO.getAllSections().stream()
                .filter(s -> s.getInstructorId() == instructorId)
                .toList();
    }
   
    public Section getSectionById(int sectionId) {
        return sectionDAO.getAllSections().stream()
                .filter(s -> s.getSectionId() == sectionId)
                .findFirst()
                .orElse(null);
    }
    
}
