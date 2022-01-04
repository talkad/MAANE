package Domain.UsersManagment;

public enum Permissions {
    /** Guest **/
    LOGIN,

    /** Registered User **/
    LOGOUT,
    UPDATE_INFO,
    CHANGE_PASSWORD,

    /** Instructor User **/
    FILL_MONTHLY_REPORT,

    /** Supervisor User **/
    ASSIGN_SCHOOLS_TO_USER,
    REGISTER_USER,
    VIEW_INSTRUCTORS_INFO,
    VIEW_USERS_INFO,
    SURVEY_MANAGEMENT,
    GENERATE_WORK_PLAN,
    REMOVE_SCHOOLS_FROM_USER,
    GET_GOALS,
    ADD_GOALS,

    /** System Manager User **/
    REGISTER_SUPERVISOR,
    REGISTER_BY_ADMIN,
    GET_ALL_SUPERVISORS,
    VIEW_ALL_USERS_INFO,
    REMOVE_USER,
    CHANGE_PASSWORD_TO_USER
}
