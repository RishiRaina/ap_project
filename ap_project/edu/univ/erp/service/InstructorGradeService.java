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
        // role + maintenance check
        AccessControl.assertAllowedWithMaintenance(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.ENTER_SCORES);

        // basic validation
        if (score < 0 || score > 100) {
            throw new AccessException("Score must be between 0 and 100.");
        }

        // ownership check
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) throw new AccessException("Invalid enrollment.");

        Section sec = sectionDAO.getSectionById(e.getSectionId());
        if (sec == null) throw new AccessException("Section not found.");

        AccessControl.assertInstructorOwnsSection(instructorId, sec.getInstructorId(), AccessControl.Actions.ENTER_SCORES);

        // add or update component grade
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


    public String autoComputeFinalLetterGrade(int enrollmentId) throws AccessException {

        int instructorId = SessionManager.getCurrentUserId();
        // Access + maintenance
        AccessControl.assertAllowedWithMaintenance(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.COMPUTE_FINAL_GRADES);

        // Ownership check
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null){
            throw new AccessException("Invalid enrollment.");
        }

        Section sec = sectionDAO.getSectionById(e.getSectionId());
        if (sec == null) {
            throw new AccessException("Section not found.");
        }

        AccessControl.assertInstructorOwnsSection(instructorId, sec.getInstructorId(), AccessControl.Actions.COMPUTE_FINAL_GRADES);

        // fetch grade component by component
        List<Grade> list = gradeDAO.getGradesByEnrollment(enrollmentId);
        Double A = null, Q = null, P = null, M = null, Efinal = null;
        for (Grade g : list) {
            String comp = g.getComponent().toUpperCase();
            switch (comp) {
                case "ASSIGNMENTS": A = g.getScore(); break;
                case "QUIZZES": Q = g.getScore(); break;
                case "PROJECT": P = g.getScore(); break;
                case "MID": M = g.getScore(); break;
                case "END": Efinal = g.getScore(); break;
            }
        }

        // Validate presence
        if (A==null || Q==null || P ==null || M==null|| Efinal==null) {
            throw new AccessException("All 5 components need to have valid values for computing final grade ");
        }
        //logic apply here
        double finalScore = A*0.15 + Q*0.15 + P*0.10 + M*0.25 + Efinal*0.35;
        // Convert to letter
        String letter;
        if (finalScore >= 85) {
            letter = "A";
        }
        else if (finalScore >= 70) {letter = "B";}
        else if (finalScore >= 55) letter = "C";
        else if (finalScore >= 30) letter = "D";
        else letter = "F";

        // Save
        boolean updated = false;
        for (Grade g : list) {
            if (g.getComponent().equalsIgnoreCase("FINAL")) {
                g.setFinalGrade(letter);
                updated = gradeDAO.updateGrade(g);
                return letter;
            }
        }
        Grade g = new Grade(enrollmentId, "FINAL", finalScore, letter);
        gradeDAO.addGrade(g);
        return letter;
    }
}
