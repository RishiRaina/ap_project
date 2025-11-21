package edu.univ.erp.access;

import java.util.*;

public class AccessControl {

    public enum Role{
        ADMIN,INSTRUCTOR,STUDENT
    }

    public enum Actions{
        LOGIN,
        LOGOUT,


        VIEW_CATALOG,
        REGISTER_SECTION,
        DROP_SECTION,
        VIEW_TIMETABLE,
        VIEW_GRADES,
        DOWNLOAD_TRANSCRIPT_CSV,
        DOWNLOAD_TRANSCRIPT_PDF,


       

        VIEW_SECTIONS,
        ENTER_SCORES,
        COMPUTE_FINAL_GRADES,
        CLASS_STATS,
        EXPORT_GRADES_CSV,
        IMPORT_GRADES_CSV,


        

        ADD_USERS,
        CREATE_COURSE,
        EDIT_COURSE,
        CREATE_SECTION,
        EDIT_SECTION,
        ASSIGN_INSTRUCTOR,
        TOGGLE_MAINTENANCE,
        BACKUP_DB,
        RESTORE_DB,

        
        EXPORT_CLASS_LIST_CSV,
        EXPORT_TRANSCRIPT_CSV,
        EXPORT_TRANSCRIPT_PDF,

        
        CHANGE_PASSWORD,
        LOCK_ACCOUNT

    }

    

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



    public static boolean isAllowed(Role role, Actions action) {
        return role_permissions.getOrDefault(role, Set.of()).contains(action);
    }

    public static void assertAllowed(Role role, Actions action) throws AccessException {
        if (!isAllowed(role, action))
            throw new AccessException("Access Denied: " + role + " cannot perform " + action + ".");
    }

   
    public static void assertAllowedWithMaintenance(Role role, Actions action) throws AccessException {
        boolean isMutating = ischangingaction(action);
        if (MaintenanceChecker.isMaintenanceOn() && role != Role.ADMIN && isMutating)
            throw new AccessException("Maintenance is ON. Only admins can modify data right now.");
        assertAllowed(role, action);
    }

   
    private static boolean ischangingaction(Actions action) {
        return Set.of(
                Actions.REGISTER_SECTION, Actions.DROP_SECTION, Actions.ENTER_SCORES, Actions.COMPUTE_FINAL_GRADES, Actions.ADD_USERS, Actions.CREATE_COURSE, Actions.EDIT_COURSE,
                Actions.CREATE_SECTION, Actions.EDIT_SECTION, Actions.ASSIGN_INSTRUCTOR, Actions.TOGGLE_MAINTENANCE, Actions.BACKUP_DB, Actions.RESTORE_DB, Actions.IMPORT_GRADES_CSV
        ).contains(action);
    }

    public static void assertInstructorOwnsSection( int currentId, int sectionInstructorId, Actions action) throws AccessException {
        if (!isAllowed(Role.INSTRUCTOR, action))
            throw new AccessException("Invalid action: " + action + " is not permitted for instructors.");
        if (currentId != sectionInstructorId)
            throw new AccessException("Access Denied: You do not have permission to modify this section.");
    }


    public static void assertStudentOwnsEnrollment(int currentId, int enrollmentStudentId, Actions action) throws AccessException {
        if (!isAllowed(Role.STUDENT, action))
            throw new AccessException("Invalid action: " + action + " is not permitted for students.");
        if (currentId != enrollmentStudentId)
            throw new AccessException("Access Denied: You are not enrolled in this section.");
    }


    public static Set<Actions> getAllowedActionsFor(Role role) {
        return Collections.unmodifiableSet(role_permissions.getOrDefault(role, Set.of()));
    }




}
