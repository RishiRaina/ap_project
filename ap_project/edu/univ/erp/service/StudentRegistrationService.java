package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.time.LocalDate;
import java.util.List;

public class StudentRegistrationService {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private CourseDAO courseDAO = new CourseDAO();

    //registering for section
    public void register(int studentId, int sectionId) throws AccessException {AccessControl.assertAllowedWithMaintenance(AccessControl.Role.STUDENT, AccessControl.Actions.REGISTER_SECTION);

        // Ensure student modifies only their own enrollments
        if (studentId != SessionManager.getCurrentUserId())
            throw new AccessException("You can only register yourself. Cannot register for someone else!");

        //for section which we use to get course details
        Section sec = sectionDAO.getSectionById(sectionId);
        if (sec == null)
            throw new AccessException("Invalid section.");

        //duplicacy check
        List<Enrollment> existing = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : existing) {
            if (e.getSectionId() == sectionId)
                throw new AccessException("Already registered in this section.");
        }

        // capacity check
        int enrolledCount = enrollmentDAO.getEnrollmentsBySection(sectionId).size();
        if (enrolledCount >= sec.getCapacity())
            throw new AccessException("Section is full.");

        // deadline check
        LocalDate today = LocalDate.now();
        if (today.isAfter(sec.getRegistrationDeadline().toLocalDate()))
            throw new AccessException("Registration deadline has passed.");

        // actual registration
        Enrollment newEnroll = new Enrollment(studentId, sectionId);
        boolean isitsuccessfull = enrollmentDAO.enrollStudent(newEnroll);
        if (!isitsuccessfull)
            throw new AccessException("Failed to register — database error.");
    }

    //this bit is for dropping a section

    public void drop(int studentId, int enrollmentId) throws AccessException {AccessControl.assertAllowedWithMaintenance(AccessControl.Role.STUDENT, AccessControl.Actions.DROP_SECTION);

        // Fetch enrollment
        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) {
            throw new AccessException("Invalid enrollment.(not in database)");
        }
        // Ownership check
        AccessControl.assertStudentOwnsEnrollment(studentId, e.getStudentId(), AccessControl.Actions.DROP_SECTION);

        // Check deadline (same as add )
        Section sec = sectionDAO.getSectionById(e.getSectionId());
        if (LocalDate.now().isAfter(sec.getRegistrationDeadline().toLocalDate()))
            throw new AccessException("Cannot drop after deadline.");

        // Drop
        boolean removed = enrollmentDAO.deleteEnrollment(enrollmentId);
        if (!removed)
            throw new AccessException("Failed to drop.");
    }

}
