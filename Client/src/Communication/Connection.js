import axios from "axios";

// TODO: secure store the JWT keys
// TODO: deal with expired tokens
// TODO: for tal: if a user doesn't have a permission don't send back 403, send back 404
// TODO: what to do in the case when a GET request fails?

class Connection{

    constructor() {
        if(Connection._instance){
            return Connection._instance;
        }

        const http = require('http');
        this.axios_instance = axios.create({
            baseURL: 'http://localhost:8080/',
            timeout: 30000, // 30 secs
            headers: {"post": {
                    "Content-Type": "text/plain",
                    "Accept": "application/json"
                }},
            httpAgent: new http.Agent({ keepAlive: true }),
        });

        this.axios_instance.interceptors.request.use(request => {
            console.log('Starting Request', JSON.stringify(request, null, 2))
            return request
        })

        Connection._instance = this;
    }

    // /**
    //  * gets the instance of the singleton class of Connection
    //  * @returns {.class} instance of the singleton class
    //  */
    // static getInstance(){
    //     if(!Connection._instance){
    //         return new Connection();
    //     }
    //
    //     return Connection._instance;
    // }

    /**
     * sends a GET request to the server
     * @param url the path to send the request to
     * @param callback a callback function to call once there's a response
     */
    sendGET(url, callback){
        const config = {
            headers: {
                'Authorization': "Bearer " + window.sessionStorage.getItem('access_token'),
                'access-control-allow-origin': "*"
            },
        }

        this.axios_instance.get(url, config)
            .then(function (response) {
                // handle success
                console.log(response);
                callback(response.data);
            })
            .catch(function (error) {
                console.log("ERROR INCOMING");
                console.log(error.response);
                if (error.response){
                    console.log("CHEKCING IT'S ERROR CODE");
                    console.log("IT'S " + error.response.status);
                    if (error.response.status === 403){
                        console.log("hello there");
                    }

                    if (error.response.status === 404) {
                        console.log("general kenobi");
                    }
                }


                // handle error
                console.log(`GET FAILED FOR ${url}`);
                console.log(error);
            })
            .then(function () {
                // always executed
            });
    }

    /**
     * sends a POST request to the server
     * @param url the path to send the request to
     * @param args arguments of the POST request
     * @param callback a callback function to call once there's a response
     */
    sendPOST(url, args, callback){
        const config = {
            headers: {
                'Authorization': "Bearer " + window.sessionStorage.getItem('access_token')
            }
        }

        this.axios_instance.post(url, args, config)
            .then(function (response) {
                // handle success
                console.log(response);
                callback(response.data)
            })
            .catch(function (error) {
                // handle error
                console.log(`POST FAILED FOR ${url} with args: ${args}`)
                console.log(error);
            });
    }

    // GENERAL USER REQUESTS

    /**
     * sends a POST request to log in the user with the given credentials
     * @param username the username of the user to log in
     * @param password the password of the user to log in
     * @param callback a callback function to call once there's a response
     */
    login(username, password, callback){
        // we'll have a different post for login since it needs a different headers

        const params = new URLSearchParams();
        params.append('username', username);
        params.append('password', password);

        const config = {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }

        this.axios_instance.post('/user/login', params, config)
            .then(function (response) {
            // handle success
            console.log(response);
            callback(response.data)
        })
            .catch(function (error) {
                // handle error
                console.log(`POST FAILED FOR '/user/login' with args: ${params}`) // TODO: remove this when done
                console.log(error);

                if (error.response){
                    if (error.response.status === 401){
                        callback({failure: "true"})
                    }
                }
            });
    }

    /**
     * sends a POST request to log out the current user
     * @param callback a callback function to call once there's a response
     */
    logout(callback){
        this.sendPOST('/user/logout', {}, callback)
    }

    /**
     * sends a POST request to authenticate the password of the current user
     * @param password the password of the current user
     * @param callback a callback function to call once there's a response
     */
    authenticatePassword(password, callback){
        this.sendPOST('/user/authenticatePassword',
            {
                password: password,
            },
            callback
        );
    }

    /**
     * sends a POST request to change the password of the current user
     * @param currentPassword the current password of the user
     * @param newPassword the new password to update to
     * @param confirmNewPassword confirmation of the new password to update
     * @param callback a callback function to call once there's a response
     */
    changePassword(currentPassword, newPassword, confirmNewPassword, callback){
        this.sendPOST('/user/changePassword',
            {
                currPassword: currentPassword,
                newPassword: newPassword,
                confirmPassword: confirmNewPassword
            },
            callback);
    }

    /**
     * sends a GET request to get the profile information from the server of the current user
     * @param callback a callback function to call once there's a response
     */
    getProfileInfo(callback){
        this.sendGET('/user/getUserInfo', callback);
    }

    /**
     * sends a POST request to change the profile info of the current user
     * @param firstName updated first name
     * @param lastName  updated last name
     * @param email updated email
     * @param phoneNumber updated phone number
     * @param city updated city
     * @param callback a callback function to call once there's a response
     */
    updateProfileInfo(firstName, lastName, email, phoneNumber, city, callback){
        this.sendPOST('/user/updateInfo',
            {
                firstName: firstName,
                lastName: lastName,
                email: email,
                phoneNumber: phoneNumber,
                city: city,
            },
            callback);
    }

    // MANAGE USERS REQUESTS

    /**
     * sends a POST request to register a new user to the system by a supervisor
     * @param usernameToRegister the username of the registered user
     * @param password the password of the registered user
     * @param userStateEnum the userStateEnum of the registered user
     * @param firstName the first name of the registered user
     * @param lastName the last name of the registered user
     * @param email the email of the registered user
     * @param phoneNumber the phone number of the registered user
     * @param city the city of the registered user
     * @param callback a callback function to call once there's a response
     */
    registerUser(usernameToRegister, password, userStateEnum, firstName, lastName, email, phoneNumber, city, callback){
        this.sendPOST('/user/registerUser',
            {
                workField: "",
                userToRegister: usernameToRegister,
                password: password,
                userStateEnum: userStateEnum,
                firstName: firstName,
                lastName: lastName,
                email: email,
                phoneNumber: phoneNumber,
                city: city,
                schools: [],
            },
            callback)
    }

    /**
     * sends a POST request to register a new user to the system by a system manager
     * @param usernameToRegister the username of the registered user
     * @param password the password of the registered user
     * @param userStateEnum the userStateEnum of the registered user
     * @param firstName the first name of the registered user
     * @param lastName the last name of the registered user
     * @param email the email of the registered user
     * @param phoneNumber the phone number of the registered user
     * @param city the city of the registered user
     * @param fieldChoice if registering a supervisor, then his field is required
     * @param optionalSupervisor if registering an instructor or general supervisor, then his supervisor is required
     * @param callback a callback function to call once there's a response
     */
    registerUserBySystemManager(usernameToRegister, password, userStateEnum, firstName, lastName, email, phoneNumber, city, fieldChoice, optionalSupervisor, callback){
        this.sendPOST('/user/registerUserBySystemManager',
            {
                user: {
                    workField: fieldChoice,
                    userToRegister: usernameToRegister,
                    password: password,
                    userStateEnum: userStateEnum,
                    firstName: firstName,
                    lastName: lastName,
                    email: email,
                    phoneNumber: phoneNumber,
                    city: city,
                    schools: [],
                },
                optionalSupervisor: optionalSupervisor,
            },
            callback);
    }

    /**
     * sends a POST request to transfer supervision to a given existing user from another existing user
     * @param currentSupervisor the supervisor from which we are revoking the supervision
     * @param newSupervisor the user to which we give supervision
     * @param callback a callback function to call once there's a response
     */
    transferSupervisionToExistingUser(currentSupervisor, newSupervisor, callback){
        this.sendPOST('/user/transferSupervisionToExistingUser',
            {
                currSupervisor: currentSupervisor,
                newSupervisor: newSupervisor,
            },
            callback)
    }

    /**
     * sends a POST request to register a new user to the system by a system manager and the user replaces a current supervisor
     * @param currentSupervisor the supervisor to be replaced
     * @param newSupervisor the supervisor to replace to (also the username of the new user)
     * @param password the password of the new user
     * @param firstName the first name of the new user
     * @param lastName the last name of the new user
     * @param email the email of the new user
     * @param phoneNumber the phone number of the new user
     * @param city the city of the new user
     * @param callback a callback function to call once there's a response
     */
    transferSuperVisionWithRegistration(currentSupervisor, newSupervisor, password, firstName, lastName, email, phoneNumber, city, callback){
        this.sendPOST('/user/transferSupervision',
            {
                currSupervisor: currentSupervisor,
                newSupervisor: newSupervisor,
                password: password,
                firstName: firstName,
                lastName: lastName,
                email: email,
                phoneNumber: phoneNumber,
                city: city,
            },
            callback);
    }

    /**
     * sends a POST request to remove a user from the system
     * @param usernameToRemove the user to remove from the system
     * @param callback a callback function to call once there's a response
     */
    removeUser(usernameToRemove, callback){

        this.sendPOST('/user/removeUser',
            {
                userToRemove: usernameToRemove
            },
            callback)
    }

    /**
     * sends a GET request for getting all the users appointed by currentUser
     * @param callback a callback function to call once there's a response
     */
    getAppointedUsers(callback){
        this.sendGET('/user/getAppointedUsers', callback);
    }

    /**
     * sends a GET request ofr getting all the users of the system (system manager call)
     * @param callback a callback function to call once there's a response
     */
    getAllUsers(callback){
        this.sendGET('/user/getAllUsers', callback);
    }

    /**
     * sends a GET request for getting all the supervisors of the system
     * @param callback a callback function to call once there's a response
     */
    getSupervisors(callback){
        this.sendGET('/user/getSupervisors', callback);
    }

    /**
     * sends a POST request to change the password to a selected user
     * @param affectedUser the user to change the password to
     * @param newPassword the new password
     * @param confirmNewPassword confirmation for the new password
     * @param callback a callback function to call once there's a response
     */
    changePasswordToUser(affectedUser, newPassword, confirmNewPassword, callback){
        this.sendPOST('/user/changePasswordToUser',
            {
                userToChangePassword: affectedUser,
                newPassword: newPassword,
                confirmPassword: confirmNewPassword
            },
            callback);
    }

    // MANAGE SCHOOLS REQUESTS

    /**
     * sends a POST request to add a coordinator to a given school
     * @param wordField the work field of the coordinator to add
     * @param firstName the first name of the coordinator to add
     * @param lastName the last name of the coordinator to add
     * @param email the email of the coordinator to add
     * @param phoneNumber the phone number of the coordinator to add
     * @param schoolID the school id to which add the new coordinator
     * @param callback a callback function to call once there's a response
     */
    addCoordinator(wordField, firstName, lastName, email, phoneNumber, schoolID, callback){
        this.sendPOST('/data/assignCoordinator',
            {
                wordField: wordField,
                firstName: firstName,
                lastName: lastName,
                email: email,
                phoneNumber: phoneNumber,
                school: schoolID,
            },
            callback);
    }

    /**
     * sends a POST request to remove a coordinator from a given school
     * @param workField the work field of the coordinator
     * @param schoolID the id of the school from which remove the coordinator
     * @param callback a callback function to call once there's a response
     */
    removeCoordinator(workField, schoolID, callback){
        this.sendPOST('/data/removeCoordinator',
            {
                workField: workField,
                school: schoolID,
            },
            callback)
    }

    // SURVEY REQUESTS

    /**
     * sends a GET request to get the survey with id surveyID
     * @param surveyID the id of the survey to get
     * @param callback a callback function to call once there's a response
     */
    getSurvey(surveyID, callback) {
        this.sendGET(`/survey/getSurvey/surveyID=${surveyID}`, callback);
    }

    /**
     * sends a POST request to the server for submitting a filled survey
     * @param id the id of the survey
     * @param answers answers to the survey
     * @param types the type of questions submitted
     * @param callback a callback function to call once there's a response
     */
    submitSurvey(id, answers, types, callback){
        this.sendPOST('/survey/submitAnswers',
            {
                id: id,
                answers: answers,
                types: types,
            },
            callback)
    }

    /**
     * sends a POST request to the server for submitting rules for a survey
     * @param surveyID the id of the survey for which the rules are for
     * @param rules the ruels
     * @param callback a callback function to call once there's a response
     */
    submitSurveyRules(surveyID, rules, callback){
        this.sendPOST('/survey/submitRules',
            {
                surveyID: surveyID,
                rules: rules,
            },
            callback)
    }

    /**
     * sends a GET request to get all the surveys created by the current active user
     * @param callback a callback function to call once there's a response
     */
    getCreatedSurveys(callback) {
        this.sendGET('/survey/getSurveys', callback);
    }

    /**
     * sends a POST request to create a new survey
     * @param id in the case of editing an already existing survey passing its id. else pass -1
     * @param title the title of the survey
     * @param description the description of the survey
     * @param questions the questions of the survey
     * @param answers the possible answers of the multiple-question questions
     * @param types the type of answer of each question
     * @param callback a callback function to call once there's a response
     */
    createSurvey(id, title, description, questions, answers, types, callback){
        this.sendPOST('/survey/createSurvey',
            {
                id: id,
                title: title,
                description: description,
                questions: questions,
                answers: answers,
                types: types
            },
            callback);
    }

    /**
     * sends a POST request to publish a survey
     * @param surveyId the id of the survey to publish
     * @param callback a callback function to call once there's a response
     */
    publishSurvey(surveyId, callback){
        this.sendPOST('/survey/submitSurvey',
            {
                surveyID: surveyId
            },
            callback)
    }

    /**
     * sends a GET request to get the statistics of a given survey
     * @param surveyID the id of the survey
     * @param callback a callback function to call once there's a response
     */
    getSurveyStats(surveyID, callback){
        this.sendGET('survey/something',
            {
                surveyID: surveyID,
            },
            callback);
    }

    /**
     * sends a GET request to get the answers of a school to a given survey
     * @param surveyID the id of the survey
     * @param schoolID the id of the school
     * @param callback a callback function to call once there's a response
     */
    getSchoolSurveyAnswers(surveyID, schoolID, callback){
        // TODO: implement
    }

    // GOALS

    /**
     * sends a GET request for getting the goals of a user for a given year
     * @param year the selected year
     * @param callback a callback function to call once there's a response
     */
    getGoals(year, callback){
        this.sendGET(`/user/getGoals/year=${year}`, callback);
    }

    /**
     * sends a POST request for adding a goal
     * @param goalDTO an object representing the goal
     * @param year the hebrew year the goal is assigned to
     * @param callback a callback function to call once there's a response
     */
    addGoal(goalDTO, year, callback){
        this.sendPOST('/user/addGoal',
            {
                goalDTO: goalDTO,
                year: year,
            },
            callback);
    }

    /**
     * sends a POST request for removing a gaol
     * @param year the year of the goal
     * @param goalId the id of the goal
     * @param callback a callback function to call once there's a response
     */
    removeGoal(year, goalId, callback){
        this.sendPOST('/user/removeGoal',
            {
                year: year,
                goalId: goalId,
            },
            callback)
    }
}

Connection._instance = null;

export default Connection;