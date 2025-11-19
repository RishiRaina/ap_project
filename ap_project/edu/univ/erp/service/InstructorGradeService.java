package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class InstructorGradeService {

    private GradeDAO gradeDAO = new GradeDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private SectionDAO sectionDAO = new SectionDAO();

    // Add or update a component grade
    public boolean addOrUpdateComponentGrade(int enrollmentId, String component, double score) throws AccessException {

        int instructorId = SessionManager.getCurrentUserId();
        //role n maintenance check
        AccessControl.assertAllowedWithMaintenance(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.ENTER_SCORES);

        // ownership check
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) throw new AccessException("Invalid enrollment.");
        //section chek
        Section sec = sectionDAO.getSectionById(e.getSectionId());
        if (sec == null) throw new AccessException("Section not found.");

        AccessControl.assertInstructorOwnsSection(instructorId, sec.getInstructorId(), AccessControl.Actions.ENTER_SCORES);

        //main add or update componet grade work
        List<Grade> existing = gradeDAO.getGradesByEnrollment(enrollmentId);
        for (Grade g : existing) {
            if (g.getComponent().equalsIgnoreCase(component)) {
                g.setScore(score);
                return gradeDAO.updateGrade(g);
            }
        }
        Grade g = new Grade(enrollmentId, component, score, null);
        return gradeDAO.addGrade(g);
    }

    public boolean saveFinalGrade(int enrollmentId, String finalGrade) throws AccessException {

        int instructorId = SessionManager.getCurrentUserId();
        // role + maintenance
        AccessControl.assertAllowedWithMaintenance(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.COMPUTE_FINAL_GRADES);

        // ownership check
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) throw new AccessException("Invalid enrollment.");

        Section sec = sectionDAO.getSectionById(e.getSectionId());
        if (sec == null) throw new AccessException("Section not found.");

        AccessControl.assertInstructorOwnsSection(instructorId, sec.getInstructorId(), AccessControl.Actions.COMPUTE_FINAL_GRADES);

        //actual saving
        List<Grade> existing = gradeDAO.getGradesByEnrollment(enrollmentId);
        // if final row exists ,update it
        for (Grade g : existing) {
            if ("FINAL".equalsIgnoreCase(g.getComponent())) {
                g.setFinalGrade(finalGrade);
                return gradeDAO.updateGrade(g);
            }
        }
        //if row not exist add a new row
        Grade g = new Grade(enrollmentId, "FINAL", 0.0, finalGrade);
        return gradeDAO.addGrade(g);
    }

    public double computeWeightedScore(List<Grade> components, double w1, double w2, double w3) {
        double total = 0;
        if (components.size() >= 1)
            total += components.get(0).getScore() * w1;
        if (components.size() >= 2)
            total += components.get(1).getScore() * w2;
        if (components.size() >= 3)
            total += components.get(2).getScore() * w3;
        return total;
    }
}
