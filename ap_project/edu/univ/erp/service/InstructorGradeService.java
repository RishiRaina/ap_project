package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;

import java.util.List;

public class InstructorGradeService {

    private GradeDAO gradeDAO = new GradeDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public boolean addOrUpdateComponentGrade(int enrollmentId, String component,double score) {

        // check if this component already exists
        List<Grade> existing = gradeDAO.getGradesByEnrollment(enrollmentId);
        for (Grade g : existing) {
            if (g.getComponent().equalsIgnoreCase(component)) {
                g.setScore(score);
                return gradeDAO.updateGrade(g);  // You may need to implement updateGrade()
            }
        }
        // if not existing add new component
        Grade g = new Grade(enrollmentId, component, score, null);
        return gradeDAO.addGrade(g);
    }

    //to save final grade
    public boolean saveFinalGrade(int enrollmentId, String finalGrade) {
        // Check if this enrollment has grades
        List<Grade> existing = gradeDAO.getGradesByEnrollment(enrollmentId);
        if (existing.isEmpty()) {
            // Create a dummy row ONLY for storing final grade
            Grade g = new Grade(enrollmentId, "FINAL", 0.0, finalGrade);
            return gradeDAO.addGrade(g);
        }
        // update grade
        for (Grade g : existing) {
            if (g.getComponent().equalsIgnoreCase("FINAL")) {
                g.setFinalGrade(finalGrade);
                return gradeDAO.updateGrade(g);
            }
        }
        // add new if not found
        Grade g = new Grade(enrollmentId, "FINAL", 0.0, finalGrade);
        return gradeDAO.addGrade(g);
    }


    // optional weighted grade calculation like 20/30/50 using 3 components
    public double computeWeightedScore(List<Grade> components, double w1, double w2, double w3) {
        double total = 0;
        if (components.size() >= 1) {
            total += components.get(0).getScore() * w1;
        }
        if (components.size() >= 2) {
            total += components.get(1).getScore() * w2;
        }
        if (components.size() >= 3) {
            total += components.get(2).getScore() * w3;
        }
        return total;
    }
}
