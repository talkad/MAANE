
let admin = {
    username: 'admin',
    password: 'admin',
}

let supervisor = {
    username: 'ronit',
    password: '1234abcd',
}

let instructor = {
    username: 'tal',
    password: '1234abcd',
}

describe('Login tests', () =>{
    beforeEach(() => {
        // visiting the login page
        cy.visit('/user/login')
    })

    it('logging in with an existing SUPERVISOR user and logging out', () => {

        // filling the form
        cy.get('input[id=login_username]').type(supervisor.username)
        cy.get('input[id=login_password]').type(supervisor.password)

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        cy.find()
        // todo: tal is a SUPERVISOR, check that its homepage contains a table of instructors

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing SYSTEM_MANAGER user and logging out', () => {

        // filling the form
        cy.get('input[id=login_username]').type('admin')
        cy.get('input[id=login_password]').type('admin')

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        // todo: admin is a SYSTEM_MANAGER, check that its homepage contains something idk

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing INSTRUCTOR user and logging out', () => {

        // filling the form
        cy.get('input[id=login_username]').type('tal')
        cy.get('input[id=login_password]').type('1234')

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        // todo: shoshi is a INSTRUCTOR, check that its homepage contains the work plan

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with a user which does not exist', () => {

        // filling the form
        cy.get('input[id=login_username]').type('yosi').should('have.value','yosi')
        cy.get('input[id=login_password]').type('1111').should('have.value','1111')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing user but with incorrect password', () => {

        // filling the form
        cy.get('input[id=login_username]').type('tal').should('have.value','tal')
        cy.get('input[id=login_password]').type('123').should('have.value','123')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('logging in and then trying to get back to the login page', () => {
        // filling the form
        cy.get('input[id=login_username]').type('tal').should('have.value','tal')
        cy.get('input[id=login_password]').type('1234').should('have.value','1234')

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        // two approaches to get back to the login page:
        // 1. using the back button of the browser: TODO: see about this case
        // cy.go('back').go('back')
        //
        // // checking that it didn't work. should stay in the home page
        // cy.url().should('not.include', '/user/login')

        // 2. typing in the url:
        cy.visit('/user/login')

        // checking that the user got redirected to the home page
        cy.url().should('include', '/user/home')
    })

    it('logging in when someone just changed that password to that account', () => {
        // TODO: programmatically change the password to the current user who tries to log in

        // filling the form
        cy.get('input[id=login_username]').type('tal').should('have.value','tal')
        cy.get('input[id=login_password]').type('1234').should('have.value','1234')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    // it('logs in programmatically without using the UI', () => {
    //     cy.request({
    //         method: 'POST',
    //         url: "http://localhost:8080/user/login",
    //         headers: {
    //             'Content-Type': 'application/x-www-form-urlencoded'
    //         },
    //         body: {
    //             username: "tal",
    //             password: "1234",
    //         }
    //     }).then((response) => {
    //         window.sessionStorage.setItem('access_token', response.body.access_token);
    //         window.sessionStorage.setItem('refresh_token', response.body.refresh_token);
    //     })
    // })
})