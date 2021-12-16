package Domain.UsersManagment;

public enum PermissionsEnum {
    /** Guest **/
    LOGIN,

    /** Registered User **/
    LOGOUT,

    /** Instructor User **/
    FILL_MONTHLY_REPORT,

    /** Supervisor User **/
    ASSIGN_SCHOOLS_TO_USER,
    REGISTER_USER,
    VIEW_INSTRUCTORS_INFO,
    CREATE_SURVEY,
    REMOVE_SURVEY,
    REMOVE_SCHOOLS_FROM_USER,
    GET_GOALS,
    ADD_GOALS,

    /** System Manager User **/
    REGISTER_SUPERVISOR,
    REMOVE_USER,
    CHANGE_PASSWORD
}
