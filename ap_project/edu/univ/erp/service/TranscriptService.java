package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.util.CSVutil;
import edu.univ.erp.util.PDFutil;

import java.io.File;
import java.util.*;

public class TranscriptService {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();


    public List<String[]> getTranscriptRows(int studentId) {
        List<String[]> rows = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : enrollments) {
            List<Grade> grades = gradeDAO.getGradesByEnrollment(e.getEnrollmentId());
            String finalGrade = extractFinalLetter(grades);
            Section sec = sectionDAO.getSectionById(e.getSectionId());
            if (sec == null) continue;
            Course c = courseDAO.getCourseById(sec.getCourseId());
            if (c == null) continue;

            String sectionInfo = (sec != null ? sec.toString() : "N/A");

            rows.add(new String[]{
                    c.getCode(),
                    c.getTitle(),
                    String.valueOf(c.getCredits()),
                    sectionInfo,
                    e.getStatus(),
                    finalGrade
            });

        }

        return rows;
    }

    public boolean exportTranscriptCSV(int studentId, File file) {
        List<String[]> rows = getTranscriptRows(studentId);
        String[] header = {"Course Code", "Title", "Credits", "Status", "Final Grade"};
        return CSVutil.writecsv(file, rows, header);
    }

    public boolean exportTranscriptPDF(int studentId, File file) {

        Student s = studentDAO.getStudentById(studentId);
        if (s == null) {return false;}
        List<String[]> rows = getTranscriptRows(studentId);
        return PDFutil.writeTranscriptPDF(file, s.getRollNo(), rows);
    }

    private String extractFinalLetter(List<Grade> grades) {

        for (Grade g : grades) {
            if ("FINAL".equalsIgnoreCase(g.getComponent())) {
                return (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) ? g.getFinalGrade() : "N/A";
            }
        }
        return "N/A";
    }
}
