
// TODO: delete if not needed anymore
class UserInfo{
    static #instance = null;


    constructor() {
        this.username = 'hello';
        this.type = 'there';
    }

    static getInstance(){
        if(this.#instance === null){
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