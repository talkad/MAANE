

class UserInfo{

    static #instance;


    constructor() {
        this.username = '';
        this.type = '';
    }

    static getInstance(){
        if(!this.#instance){
            this.#instance = new UserInfo();
        }

        return this.#instance;
    }

    setUsername(username){
        this.username = username;
    }

    getUsername(){
        return this.username;
    }

    setType(type){
        this.type = type;
    }

    getType(){
        return this.type;
    }
}

export default UserInfo;