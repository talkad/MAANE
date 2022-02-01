import axios from "axios";

// TODO: change the function in the class so they would get the args and the function warps it

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
     * @param args args of the login function
     * @param callback a callback function to call once there's a response
     */
    login(args, callback){
        this.sendPOST('/user/login', args, callback)
    }

    /**
     * sends a POST request to log out the current user
     * @param args args of the logout function
     * @param callback a callback function to call once there's a response
     */
    logout(args, callback){
        this.sendPOST('/user/logout', args, callback)
    }

    /**
     * sends a POST request to register a new user to the system
     * @param args args of the register function
     * @param callback a callback function to call once there's a response
     */
    register(args, callback){
        this.sendPOST('/user/registerUser', args, callback)
    }

    /**
     * sends a POST request to remove a user from the system
     * @param args args of the removeUser function
     * @param callback a callback function to call once there's a response
     */
    removeUser(args, callback){
        this.sendPOST('/user/removeUser', args, callback)
    }

    /**
     * sends s POST request to create a new survey
     * @param args args of the createSurvey function
     * @param callback a callback function to call once there's a response
     */
    createSurvey(args, callback){
        this.sendPOST('/survey/createSurvey', args, callback)
    }
}

export default Connection;