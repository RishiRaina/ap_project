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
    private NotificationsDAO notificationsDAO = new NotificationsDAO();


    public void register(int studentId, int sectionId) throws AccessException {

        AccessControl.assertAllowedWithMaintenance(
                AccessControl.Role.STUDENT,
                AccessControl.Actions.REGISTER_SECTION
        );

        if (studentId != SessionManager.getCurrentUserId())
            throw new AccessException("You can only register yourself.");

        Section sec = sectionDAO.getSectionById(sectionId);
        if (sec == null)
            throw new AccessException("Invalid section.");

        List<Enrollment> existing = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : existing) {
            if (e.getSectionId() == sectionId)
                throw new AccessException("Already registered in this section.");
        }

        int enrolledCount = enrollmentDAO.getEnrollmentsBySection(sectionId).size();
        if (enrolledCount >= sec.getCapacity())
            throw new AccessException("Section is full.");

        LocalDate today = LocalDate.now();
        if (today.isAfter(sec.getRegistrationDeadline().toLocalDate()))
            throw new AccessException("Registration deadline passed.");

        Enrollment newEnroll = new Enrollment(studentId, sectionId);
        boolean ok = enrollmentDAO.enrollStudent(newEnroll);

        if (!ok)
            throw new AccessException("Failed to register.");


        notificationsDAO.addNotification(new Notification(
                studentId,
                null,
                "Registration Successful",
                "You have successfully registered in section " + sectionId
        ));


        int instructorUserId = sectionDAO.getInstructorUserId(sectionId);

        notificationsDAO.addNotification(new Notification(
                instructorUserId,
                null,
                "New Student Joined",
                "A student has registered in your section " + sectionId
        ));


        int after = enrollmentDAO.getEnrollmentsBySection(sectionId).size();
        if (after == sec.getCapacity()) {

            notificationsDAO.addNotification(new Notification(
                    null,
                    "INSTRUCTOR",
                    "Section Full",
                    "Your section " + sectionId + " has reached full capacity."
            ));
        }
    }



    public void drop(int studentId, int enrollmentId) throws AccessException {

        AccessControl.assertAllowedWithMaintenance(
                AccessControl.Role.STUDENT,
                AccessControl.Actions.DROP_SECTION
        );

        Enrollment e = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null)
            throw new AccessException("Invalid enrollment.");

        AccessControl.assertStudentOwnsEnrollment(studentId, e.getStudentId(),
                AccessControl.Actions.DROP_SECTION);

        Section sec = sectionDAO.getSectionById(e.getSectionId());
        LocalDate regDeadline = sec.getRegistrationDeadline().toLocalDate();
        LocalDate dropDeadline = regDeadline.plusDays(14);

        if (LocalDate.now().isAfter(dropDeadline))
            throw new AccessException("Drop deadline passed.");

        boolean removed = enrollmentDAO.deleteEnrollment(enrollmentId);

        if (!removed)
            throw new AccessException("Drop failed.");


        notificationsDAO.addNotification(new Notification(
                studentId,
                null,
                "Dropped Section",
                "You dropped section " + sec.getSectionId()
        ));


        int instructorUserId = sectionDAO.getInstructorUserId(sec.getSectionId());

        notificationsDAO.addNotification(new Notification(
                instructorUserId,
                null,
                "Student Dropped",
                "A student has dropped your section " + sec.getSectionId()
        ));
    }
}
