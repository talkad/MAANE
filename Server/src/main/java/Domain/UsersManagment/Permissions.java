package Domain.UsersManagment;

public enum Permissions {
    /** Guest **/
    LOGIN,

    /** Registered User **/
    LOGOUT,
    UPDATE_INFO,
    VIEW_INFO,
    CHANGE_PASSWORD,

    /** Instructor User **/
    FILL_MONTHLY_REPORT,
    VIEW_WORK_PLAN,
    ADD_BASKET,

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
    REMOVE_GOALS,
    REMOVE_BASKET,
    SEND_SURVEY_EMAIL,

    /** System Manager User **/
    REGISTER_SUPERVISOR,
    REGISTER_BY_ADMIN,
    GET_ALL_SUPERVISORS,
    VIEW_ALL_USERS_INFO,
    REMOVE_USER,
    CHANGE_PASSWORD_TO_USER
}
