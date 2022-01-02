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

    static getInstance(){
        if(this.#instance === null){
            this.#instance = new Connection();
        }

        return this.#instance;
    }

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

    setUpUser(callback){
        this.sendGET('/user/startup', callback)
    }

    login(args, callback){
        this.sendPOST('/user/login', args, callback)
    }

    register(args, callback){
        this.sendPOST('/user/registerUser', args, callback)
    }

    createSurvey(args, callback){
        this.sendPOST('/survey/createSurvey', args, callback)
    }
}

export default Connection;