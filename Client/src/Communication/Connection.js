import axios from "axios";

// TODO: tal finally implemented the connection like a normal person so i need to adjust to that (normal tokens and encryption)
// TODO: do we really need to pass the "currentUser" each time? should change that so it "pushes" it implicitly
// TODO: change to GET requests where it's proper instead of the POST requests
// TODO: secure store the JWT keys
// TODO: why the classes outside doesn't recognize the methods???

class Connection{
    static #instance = null;

    constructor() {
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
    }

    /**
     * gets the instance of the singleton class of Connection
     * @returns {.class} instance of the singleton class
     */
    static getInstance(){
        if(this.#instance === null){
            this.#instance = new Connection();
        }

        return this.#instance;
    }

    /**
     * sends a GET request to the server
     * @param url the path to send the request to
     * @param callback a callback function to call once there's a response
     */
    sendGET(url, callback){
        const config = {
            headers: {
                'Authorization': window.sessionStorage.getItem('access_token')
            }
        }

        this.axios_instance.get(url, config)
            .then(function (response) {
                // handle success
                console.log(response);
                callback(response.data);
            })
            .catch(function (error) {
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
                'Authorization': window.sessionStorage.getItem('access_token')
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
     * sends a GET request to set up the user (getting guest id)
     * @param callback a callback function to call once there's a response
     */
    setUpUser(callback){
        this.sendGET('/user/startup', callback)
    }

    /**
     * sends a POST request to log in the user with the given credentials
     * @param currUser the token (mostly the user's username) of the active user in the current session
     * @param username the username of the user to log in
     * @param password the password of the user to log in
     * @param callback a callback function to call once there's a response
     */
    login(currUser, username, password, callback){
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
                console.log(`POST FAILED FOR '/user/login' with args: ${params}`)
                console.log(error);
            });
    }

    /**
     * sends a POST request to log out the current user
     * @param username the token (mostly the user's username) of the active user in the current session to log out
     * @param callback a callback function to call once there's a response
     */
    logout(username, callback){
        this.sendPOST('/user/logout',
            {
            name: username
            },
            callback)
    }

    /**
     * sends a POST request to authenticate the password of the current user
     * @param currentUser the token (mostly the user's username) of the active user in the current session to authenticate
     * @param password the password of the current user
     * @param callback a callback function to call once there's a response
     */
    authenticatePassword(currentUser, password, callback){
        this.sendPOST('/user/authenticatePassword',
            {
                currUser: currentUser,
                password: password,
            },
            callback
        );
    }

    /**
     * sends a POST request to change the password of the current user
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param currentPassword the current password of the user
     * @param newPassword the new password to update to
     * @param confirmNewPassword confirmation of the new password to update
     * @param callback a callback function to call once there's a response
     */
    changePassword(currentUser, currentPassword, newPassword, confirmNewPassword, callback){
        this.sendPOST('/user/changePassword',
            {
                currUser: currentUser,
                currPassword: currentPassword,
                newPassword: newPassword,
                confirmPassword: confirmNewPassword
            },
            callback);
    }

    /**
     * sends a POST request to get the profile information from the server of the current user
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param callback a callback function to call once there's a response
     */
    getProfileInfo(currentUser, callback){
        this.sendPOST('/user/getUserInfo',
            {
                currUser: currentUser,
            },
            callback);
    }

    /**
     * sends a POST request to change the profile info of the current user
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param firstName updated first name
     * @param lastName  updated last name
     * @param email updated email
     * @param phoneNumber updated phone number
     * @param city updated city
     * @param callback a callback function to call once there's a response
     */
    updateProfileInfo(currentUser, firstName, lastName, email, phoneNumber, city, callback){
        this.sendPOST('/user/updateInfo',
            {
                currUser: currentUser,
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
     * @param currUser the token (mostly the user's username) of the active user in the current session
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
    registerUser(currUser, usernameToRegister, password, userStateEnum, firstName, lastName, email, phoneNumber, city, callback){
        this.sendPOST('/user/registerUser',
            {
                currUser: currUser,
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
     * @param currUser the token (mostly the user's username) of the active user in the current session
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
    registerUserBySystemManager(currUser, usernameToRegister, password, userStateEnum, firstName, lastName, email, phoneNumber, city, fieldChoice, optionalSupervisor, callback){
        this.sendPOST('/user/registerUserBySystemManager',
            {
                user: {
                    currUser: currUser,
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
            callback)
    }

    /**
     * sends a POST request to remove a user from the system
     * @param currUser the token (mostly the user's username) of the active user in the current session
     * @param usernameToRemove the user to remove from the system
     * @param callback a callback function to call once there's a response
     */
    removeUser(currUser, usernameToRemove, callback){
        this.sendPOST('/user/removeUser',
            {
                currUser: currUser,
                userToRemove: usernameToRemove
            },
            callback)
    }

    /**
     * sends a POST request for getting all the users appointed by currentUser
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param callback a callback function to call once there's a response
     */
    getAppointedUsers(currentUser, callback){
        this.sendGET('/user/getAppointedUsers', callback);
    }

    /**
     * sends a POST request ofr getting all the users of the system (system manager call)
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param callback a callback function to call once there's a response
     */
    getAllUsers(currentUser, callback){
        this.sendPOST('/user/getAllUsers',
            {
                currUser: currentUser,
            },
            callback);
    }

    /**
     * sends a POST request for getting all the supervisors of the system
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param callback a callback function to call once there's a response
     */
    getSupervisors(currentUser, callback){
        this.sendPOST('/user/getSupervisors',
            {
                currUser: currentUser,
            },
            callback);
    }

    /**
     * sends a POST request to change the password to a selected user
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param affectedUser the user to change the password to
     * @param newPassword the new password
     * @param confirmNewPassword confirmation for the new password
     * @param callback a callback function to call once there's a response
     */
    changePasswordToUser(currentUser, affectedUser, newPassword, confirmNewPassword, callback){
        this.sendPOST('/user/changePasswordToUser',
            {
                currUser: currentUser,
                userToChangePassword: affectedUser,
                newPassword: newPassword,
                confirmPassword: confirmNewPassword
            },
            callback);
    }

    // MANAGE SCHOOLS REQUESTS

    /**
     * sends a POST request to add a coordinator to a given school
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param wordField the work field of the coordinator to add
     * @param firstName the first name of the coordinator to add
     * @param lastName the last name of the coordinator to add
     * @param email the email of the coordinator to add
     * @param phoneNumber the phone number of the coordinator to add
     * @param schoolID the school id to which add the new coordinator
     * @param callback a callback function to call once there's a response
     */
    addCoordinator(currentUser, wordField, firstName, lastName, email, phoneNumber, schoolID, callback){
        this.sendPOST('/data/assignCoordinator',
            {
                currUser: currentUser,
                wordField: wordField,
                firstName: firstName,
                lastName: lastName,
                email: email,
                phoneNumber: phoneNumber,
                school: schoolID,
            },
            callback);
    }

    // SURVEY REQUESTS

    /**
     * sends a POST request to create a new survey
     * @param title the title of the survey
     * @param description the description of the survey
     * @param questions the questions of the survey
     * @param answers the possible answers of the multiple-question questions
     * @param types the type of answer of each question
     * @param callback a callback function to call once there's a response
     */
    createSurvey(title, description, questions, answers, types, callback){
        this.sendPOST('/survey/createSurvey',
            {
                id: -1,
                title: title,
                description: description,
                questions: questions,
                answers: answers,
                types: types
            },
            callback);
    }

    // GOALS

    /**
     * sends a POST request for getting the goals of a user for a given year
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param year the selected year
     * @param callback a callback function to call once there's a response
     */
    getGoals(currentUser, year, callback){
        this.sendPOST('/user/getGoals',
            {
                currUser: currentUser,
                year: year,
            },
            callback);
    }

    /**
     * sends a POST request for adding a goal
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param goalDTO an object representing the goal
     * @param year the hebrew year the goal is assigned to
     * @param callback a callback function to call once there's a response
     */
    addGoal(currentUser, goalDTO, year, callback){
        this.sendPOST('/user/addGoal',
            {
                currUser: currentUser,
                goalDTO: goalDTO,
                year: year,
            },
            callback);
    }

    /**
     * sends a POST request for removing a gaol
     * @param currentUser the token (mostly the user's username) of the active user in the current session
     * @param year the year of the goal
     * @param goalId the id of the goal
     * @param callback a callback function to call once there's a response
     */
    removeGoal(currentUser, year, goalId, callback){
        this.sendPOST('/user/removeGoal',
            {
                currUser: currentUser,
                year: year,
                goalId: goalId,
            },
            callback)
    }
}

export default Connection;