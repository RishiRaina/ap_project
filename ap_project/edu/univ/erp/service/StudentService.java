package edu.univ.erp.service;

import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserAuth;

import java.util.List;

public class StudentService {

    private StudentDAO studentDAO;
    private UserAuthDAO userAuthDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.userAuthDAO = new UserAuthDAO();
    }

    public boolean addStudent(Student student) {
        UserAuth user = userAuthDAO.getUserByUsername(String.valueOf(student.getUserId()));
        if (user == null) return false;

        List<Student> all = studentDAO.getAllStudents();
        for (Student s : all) {
            if (s.getRollNo().equals(student.getRollNo())) return false;
        }

        if (student.getProgram() == null || student.getProgram().isBlank()) return false;
        if (student.getYear() <= 0 || student.getYear() > 6) return false;

        return studentDAO.addStudent(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }
}
