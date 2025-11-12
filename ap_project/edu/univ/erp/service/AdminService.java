package edu.univ.erp.service;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.UserAuth;
import edu.univ.erp.domain.Course;

public class AdminService {
    private UserAuthDAO userAuthDAO;
    private CourseDAO courseDAO;

    public AdminService() {
        userAuthDAO = new UserAuthDAO();
        courseDAO = new CourseDAO();
    }

    public boolean addUser(UserAuth user) {
        return userAuthDAO.addUser(user);
    }

    public boolean addCourse(Course c) {
        return courseDAO.addCourse(c);
    }

    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }
}
