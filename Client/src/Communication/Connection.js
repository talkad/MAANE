import axios from "axios";

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
        this.axios_instance.get(url)
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
        this.axios_instance.post(url, args)
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

    /**
     * sends a GET request to set up the user (getting guest id)
     * @param callback a callback function to call once there's a response
     */
    setUpUser(callback){
        this.sendGET('/user/startup', callback)
    }

    /**
     * sends a POST request to log in the user with the given credentials
     * @param currUser the current logged-in user
     * @param username the username of the user to log in
     * @param password the password of the user to log in
     * @param callback a callback function to call once there's a response
     */
    login(currUser, username, password, callback){
        this.sendPOST('/user/login',
            {
                currUser: currUser,
                userToLogin: username,
                password: password
            },
            callback)
    }

    /**
     * sends a POST request to log out the current user
     * @param username the username of the user to log out
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
     * sends a POST request to register a new user to the system
     * @param currUser the current user which is logged in
     * @param usernameToRegister the username of the registered user
     * @param password the password of the registered user
     * @param userStateEnum the userStateEnum of the registered user
     * @param callback a callback function to call once there's a response
     */
    register(currUser, usernameToRegister, password, userStateEnum, callback){
        this.sendPOST('/user/registerUser',
            {
                currUser: currUser,
                userToRegister: usernameToRegister,
                password: password,
                userStateEnum: userStateEnum,
                firstName: "",
                lastName: "",
                email: "",
                phoneNumber: "",
                city: ""
            },
            callback)
    }

    /**
     * sends a POST request to remove a user from the system
     * @param currUser the current user active
     * @param usernameToRemove the user to remove from the system
     * @param callback a callback function to call once there's a response
     */
    removeUser(currUser, usernameToRemove, callback){
        this.sendPOST('/user/removeUser',
            {
                currUser: currUser,
                usernameToRemove: usernameToRemove
            },
            callback)
    }

    /**
     * sends s POST request to create a new survey
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
            callback)
    }
}

export default Connection;