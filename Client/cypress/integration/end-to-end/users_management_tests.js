
describe('User management test as a supervisor', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor
        cy.visit('/user/home')
    })
})

describe('User management test as a system manager', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a system manager
        cy.visit('/user/home')
    })
})