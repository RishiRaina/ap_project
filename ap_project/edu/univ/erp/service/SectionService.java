package edu.univ.erp.service;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;

import java.sql.Date;
import java.util.List;

public class SectionService {

    private SectionDAO sectionDAO;

    public SectionService() {
        this.sectionDAO = new SectionDAO();
    }
    
    public boolean addSection(int courseId,
                              Integer instructorId,
                              String dayTime,
                              String room,
                              int capacity,
                              String semester,
                              int year,
                              Date registrationDeadline) {

        Section s = new Section(
                courseId,
                instructorId,
                dayTime,
                room,
                capacity,
                semester,
                year,
                registrationDeadline
        );

        return sectionDAO.addSection(s);
    }

    public List<Section> getAllSections() {
        return sectionDAO.getAllSections();
    }
}
