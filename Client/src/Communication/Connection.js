



class Connection{
    static usermame = "";
    static http;
    static agent;

    static setConnection(){
        this.http = require('http');
        this.agent = new this.http.Agent({ keepAlive: true });
    }

    static setUsername(username){
        this.username = username;
    }

    static setUpUser(callback){
        var options = {
            host: 'http://localhost',
            port: 8080,
            path: '/user/startup',
            method: "GET",
            agent: this.agent,
        };

        this.http.request(options, callback).on("error", function (err) {console.log('ffs'); console.log(err);}).end();
    }
}

export default Connection;