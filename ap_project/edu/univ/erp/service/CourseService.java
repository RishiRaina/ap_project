package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Course;
import java.util.List;

public class CourseService {
    private CourseDAO courseDAO;

    public CourseService() {
        courseDAO = new CourseDAO();
    }

    public boolean addCourse(Course c) {
        return courseDAO.addCourse(c);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }
}
