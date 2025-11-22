package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.util.CSVutil;
import edu.univ.erp.util.PDFutil;

import java.io.File;
import java.util.*;

public class TranscriptService {

    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;

    public TranscriptService() {
        enrollmentDAO = new EnrollmentDAO();
        gradeDAO = new GradeDAO();
        sectionDAO = new SectionDAO();
        courseDAO = new CourseDAO();
        studentDAO = new StudentDAO();
    }


     //One row = one section enrollment + final grade.
    public List<String[]> getTranscriptRows(int studentId) {

        List<String[]> rows = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : enrollments) {

            //calculates the grade for the enrollment
            List<Grade> grades = gradeDAO.getGradesByEnrollment(e.getEnrollmentId());
            String finalGrade = extractFinalLetter(grades);


            //find the course via section id
            Section sec = sectionDAO.getSectionById(e.getSectionId());
            if (sec == null) continue;
            Course c = courseDAO.getCourseById(sec.getCourseId());
            if (c == null) continue;
            rows.add(new String[]{
                    c.getCode(),
                    c.getTitle(),
                    String.valueOf(c.getCredits()),
                    e.getStatus(),
                    finalGrade
            });
        }

        return rows;
    }


    //exporting transcript as csv
    public boolean exportTranscriptCSV(int studentId, File file) {
        List<String[]> rows = getTranscriptRows(studentId);

        String[] header = {"Course Code", "Title", "Credits", "Status", "Final Grade"};

        return CSVutil.writecsv(file, rows, header);
    }


    //exporting transcript as pdf
    public boolean exportTranscriptPDF(int studentId, File file) {

        Student student = studentDAO.getStudentById(studentId);
        if (student == null) return false;

        List<String[]> rows = getTranscriptRows(studentId);

        return PDFutil.writeTranscriptPDF(
                file,
                student.getRollNo(),
                rows
        );
    }

    private String extractFinalLetter(List<Grade> grades) {
        for (Grade g : grades) {
            if ("FINAL".equalsIgnoreCase(g.getComponent())) {
                return (g.getFinalGrade() != null) ? g.getFinalGrade() : "N/A";
            }
        }
        return "N/A";
    }

}
