



class Connection{
    static usermame = "";
    // static http;
    // static agent;
    // static

    static setConnection(){
        this.http = require('http');
        this.axiosInstance = axios.create({
            baseURL: 'http://localhost:8080/',
            timeout: 1000,
            headers: {'X-Custom-Header': 'foobar'}
          });
        this.agent = new this.http.Agent({ keepAlive: true });
    }

    static setUsername(username){
        this.username = username;
    }

    static setUpUser(callback){
        axios.get('/user?ID=12345')
        .then(function (response) {
        // handle success
            console.log(response);
            callback(callback);
        })
        .catch(function (error) {
        // handle error
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

        // this.http.request(options, callback).on("error", function (err) {console.log('ffs'); console.log(err);}).end();
    }
}

export default Connection;