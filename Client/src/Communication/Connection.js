import axios from "axios";


class Connection{
    static usermame = "";
    // static http;
    // static agent;
    // constructor() {
    //     this.http = require('http');
    //     // this.axios = require('axios');
    //     this.axios_instance = axios.create({
    //         baseURL: 'http://localhost:8080/',
    //         timeout: 1000,
    //         headers: {'X-Custom-Header': 'foobar'}
    //     });
    //     this.agent = new this.http.Agent({ keepAlive: true });
    // }

    static setConnection(){
        this.http = require('http');
        // this.axios = require('axios');
        this.axios_instance = axios.create({
            baseURL: 'http://localhost:8080/',
            timeout: 30000, // 30 secs
            httpAgent: new this.http.Agent({ keepAlive: true }),
        });
        // this.agent = new this.http.Agent({ keepAlive: true });
    }

    static setUsername(username){
        this.username = username;
    }

    static setUpUser(callback){
        // axios.get('http://localhost:8080/user/startup')
        //     .then((response) => {
        //         console.log(response.data);
        //         console.log(response.status);
        //         console.log(response.statusText);
        //         console.log(response.headers);
        //         console.log(response.config);
        //     }).catch(function (error) {
        //     // handle error
        //     console.log("general kenobi");
        //     console.log(error);
        // });
        this.axios_instance.get('/user/startup')
            .then(function (response) {
                // handle success
                console.log(response);
                callback(response.data);
            })
            .catch(function (error) {
                // handle error
                console.log("ERROR");
                console.log(error);
            })
            .then(function () {
                // always executed
            });


        // var options = {
        //     host: 'http://localhost',
        //     port: 8080,
        //     path: '/user/startup',
        //     method: "GET",
        //     agent: this.agent,
        // };
        //
        // this.http.request(options, callback).on("error", function (err) {console.log('ffs'); console.log(err);}).end();
    }
}

export default Connection;