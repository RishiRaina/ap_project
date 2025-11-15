package edu.univ.erp.access;

import java.util.*;

public class AccessControl {

    public enum Role{
        ADMIN,INSTRUCTOR,STUDENT
    }

    public enum Actions{
        // the ones common to all

        LOGIN,
        LOGOUT,


        //STUDENT ones only

        VIEW_CATALOG,
        REGISTER_SECTION,
        DROP_SECTION,
        VIEW_TIMETABLE,
        VIEW_GRADES,
        DOWNLOAD_TRANSCRIPT_CSV,
        DOWNLOAD_TRANSCRIPT_PDF,


        //INSTRUCTOR ones only

        VIEW_SECTIONS,
        ENTER_SCORES,
        COMPUTE_FINAL_GRADES,
        CLASS_STATS,
        EXPORT_GRADES_CSV,
        IMPORT_GRADES_CSV,


        //ADMIN only

        ADD_USERS,
        CREATE_COURSE,
        EDIT_COURSE,
        CREATE_SECTION,
        EDIT_SECTION,
        ASSIGN_INSTRUCTOR,
        TOGGLE_MAINTENANCE,
        BACKUP_DB,
        RESTORE_DB,

        // Reporting actions
        EXPORT_CLASS_LIST_CSV,
        EXPORT_TRANSCRIPT_CSV,
        EXPORT_TRANSCRIPT_PDF,

        // Auth/Security (bonus)
        CHANGE_PASSWORD,
        LOCK_ACCOUNT

    }

    //now role mapping

    private static final Map<Role,Set<Actions>> role_permissions=new HashMap<>();

    static{

        role_permissions.put(Role.ADMIN,Set.of(
                Actions.LOGIN,Actions.LOGOUT,
                Actions.ADD_USERS,Actions.CREATE_COURSE,Actions.EDIT_COURSE,Actions.CREATE_SECTION,Actions.EDIT_SECTION,
                Actions.ASSIGN_INSTRUCTOR,Actions.TOGGLE_MAINTENANCE, Actions.BACKUP_DB,Actions.RESTORE_DB,
                Actions.EXPORT_CLASS_LIST_CSV,Actions.EXPORT_TRANSCRIPT_CSV,Actions.EXPORT_TRANSCRIPT_PDF,
                Actions.CHANGE_PASSWORD, Actions.LOCK_ACCOUNT
        ));

        role_permissions.put(Role.INSTRUCTOR,Set.of(
                Actions.LOGIN,Actions.LOGOUT,
                Actions.VIEW_SECTIONS,Actions.ENTER_SCORES,Actions.COMPUTE_FINAL_GRADES,
                Actions.CLASS_STATS,Actions.EXPORT_GRADES_CSV, Actions.IMPORT_GRADES_CSV,
                Actions.EXPORT_CLASS_LIST_CSV, Actions.EXPORT_TRANSCRIPT_CSV, Actions.EXPORT_TRANSCRIPT_PDF,
                Actions.CHANGE_PASSWORD));

        role_permissions.put(Role.STUDENT, Set.of(
                Actions.LOGIN, Actions.LOGOUT,
                Actions.VIEW_CATALOG, Actions.REGISTER_SECTION, Actions.DROP_SECTION,
                Actions.VIEW_TIMETABLE, Actions.VIEW_GRADES,
                Actions.DOWNLOAD_TRANSCRIPT_CSV, Actions.DOWNLOAD_TRANSCRIPT_PDF,
                Actions.EXPORT_TRANSCRIPT_CSV, Actions.EXPORT_TRANSCRIPT_PDF,
                Actions.CHANGE_PASSWORD
        ));

    }

    // VV IMP FROM HERE DEPLAG AND UNDESTAND SPECIALLY ISMUTATING ACTION
    public static boolean isMaintenanceOn() {
        // Call your actual maintenance flag from Settings or stub
        return MaintenanceChecker.isMaintenanceOn();
    }


    public static boolean isAllowed(Role role, Actions action) {
        return role_permissions.getOrDefault(role, Set.of()).contains(action);// getordefault returns a set of null elements if not present doesnt go into error
    }

    public static void assertAllowed(Role role, Actions action) throws AccessException {
        if (!isAllowed(role, action))
            throw new AccessException("Access Denied: " + role + " cannot perform " + action + ".");
    }

    //this method is for in case of maintenance data changing tasks(which can be found by ischangingaction) can be performed only by admin
    public static void assertAllowedWithMaintenance(Role role, Actions action) throws AccessException {
        // Only ADMIN can change state during maintenance
        boolean isMutating = ischangingaction(action);
        if (isMaintenanceOn() && role != Role.ADMIN && isMutating)
            throw new AccessException("Maintenance is ON. Only admins can modify data right now.");
        assertAllowed(role, action);
    }

    /** Helper to identify actions that change state (not just view/report) */
    private static boolean ischangingaction(Actions action) {
        // Only certain actions mutate system DB state
        return Set.of(
                Actions.REGISTER_SECTION, Actions.DROP_SECTION, Actions.ENTER_SCORES, Actions.COMPUTE_FINAL_GRADES, Actions.ADD_USERS, Actions.CREATE_COURSE, Actions.EDIT_COURSE,
                Actions.CREATE_SECTION, Actions.EDIT_SECTION, Actions.ASSIGN_INSTRUCTOR, Actions.TOGGLE_MAINTENANCE, Actions.BACKUP_DB, Actions.RESTORE_DB, Actions.IMPORT_GRADES_CSV
        ).contains(action);
    }

    //ownership enforcement

    //Ensures that the current instructor is performing a valid instructor action and is the assigned owner of the given section.
    // Used for actions like ENTER_SCORES, COMPUTE_FINAL_GRADES, etc.
    public static void assertInstructorOwnsSection( int currentId, int sectionInstructorId, Actions action) throws AccessException {
        if (!isAllowed(Role.INSTRUCTOR, action))
            throw new AccessException("Invalid action: " + action + " is not permitted for instructors.");
        if (currentId != sectionInstructorId)
            throw new AccessException("Access Denied: You do not have permission to modify this section.");
    }

    //Ensures that the current student is performing a valid student action and that the enrollment being modified belongs to them.

    public static void assertStudentOwnsEnrollment(int currentId, int enrollmentStudentId, Actions action) throws AccessException {
        if (!isAllowed(Role.STUDENT, action))
            throw new AccessException("Invalid action: " + action + " is not permitted for students.");
        if (currentId != enrollmentStudentId)
            throw new AccessException("Access Denied: You are not enrolled in this section.");
    }

    //permission listing , this helps later on for UI

    public static Set<Actions> getAllowedActionsFor(Role role) {
        return Collections.unmodifiableSet(role_permissions.getOrDefault(role, Set.of()));
    }




}
