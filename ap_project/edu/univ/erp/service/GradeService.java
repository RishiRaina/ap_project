package edu.univ.erp.service;

import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.Grade;
import java.util.List;

public class GradeService {
    private GradeDAO gradeDAO;

    public GradeService() {
        gradeDAO = new GradeDAO();
    }

    public boolean addGrade(Grade g) {
        return gradeDAO.addGrade(g);
    }

    public List<Grade> getGradesByEnrollment(int enrollmentId) {
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }

}
