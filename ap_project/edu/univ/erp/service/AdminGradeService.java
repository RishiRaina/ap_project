package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class AdminGradeService {

    private GradeDAO gradeDAO = new GradeDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    // Admin manually enters or updates component marks
    public boolean addOrUpdateComponentGrade(int enrollmentId, String component, double score) throws AccessException {

        AccessControl.assertAllowed(AccessControl.Role.ADMIN, AccessControl.Actions.ENTER_SCORES);

        if (score < 0 || score > 100)
            throw new AccessException("Score must be 0–100.");

        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null)
            throw new AccessException("Invalid enrollment.");

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

    // Admin manually assigns a final letter grade
    public boolean saveFinalGrade(int enrollmentId, String finalGrade) throws AccessException {

        AccessControl.assertAllowed(AccessControl.Role.ADMIN, AccessControl.Actions.COMPUTE_FINAL_GRADES);

        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null)
            throw new AccessException("Invalid enrollment.");

        List<Grade> list = gradeDAO.getGradesByEnrollment(enrollmentId);

        for (Grade g : list) {
            if (g.getComponent().equalsIgnoreCase("FINAL")) {
                g.setFinalGrade(finalGrade);
                return gradeDAO.updateGrade(g);
            }
        }

        Grade g = new Grade(enrollmentId, "FINAL", 0.0, finalGrade);
        return gradeDAO.addGrade(g);
    }

    // Admin auto-computes final letter grade based on components
    public String autoComputeFinalLetterGrade(int enrollmentId) throws AccessException {

        AccessControl.assertAllowed(AccessControl.Role.ADMIN, AccessControl.Actions.COMPUTE_FINAL_GRADES);

        List<Grade> list = gradeDAO.getGradesByEnrollment(enrollmentId);

        Double A = null, Q = null, P = null, M = null, E = null;

        for (Grade g : list) {
            switch (g.getComponent().toUpperCase()) {
                case "ASSIGNMENTS": A = g.getScore(); break;
                case "QUIZZES": Q = g.getScore(); break;
                case "PROJECT": P = g.getScore(); break;
                case "MID": M = g.getScore(); break;
                case "END": E = g.getScore(); break;
            }
        }

        if (A == null || Q == null || P == null || M == null || E == null)
            throw new AccessException("Missing components.");

        double finalScore = A*0.15 + Q*0.15 + P*0.10 + M*0.25 + E*0.35;

        String letter =
                finalScore >= 85 ? "A" :
                        finalScore >= 70 ? "B" :
                                finalScore >= 55 ? "C" :
                                        finalScore >= 30 ? "D" : "F";

        for (Grade g : list) {
            if (g.getComponent().equalsIgnoreCase("FINAL")) {
                g.setFinalGrade(letter);
                gradeDAO.updateGrade(g);
                return letter;
            }
        }

        Grade g = new Grade(enrollmentId, "FINAL", finalScore, letter);
        gradeDAO.addGrade(g);

        return letter;
    }
}
