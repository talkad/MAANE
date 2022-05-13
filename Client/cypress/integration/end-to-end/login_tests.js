
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

        cy.get(`#td_non_supervisor_username_${instructor.username}`) // looking for an instructor element under the supervisor

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing SYSTEM_MANAGER user and logging out', () => {

        // filling the form
        cy.get('input[id=login_username]').type(admin.username)
        cy.get('input[id=login_password]').type(admin.password)

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        cy.get(`#td_supervisor_username_${supervisor.username}`) // looking for a supervisor element under the system manager

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing INSTRUCTOR user and logging out', () => {

        // filling the form
        cy.get('input[id=login_username]').type(instructor.username)
        cy.get('input[id=login_password]').type(instructor.password)

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        let page_title = "לוח העבודה שלי";
        cy.contains(page_title)

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in with a user which does not exist', () => {

        // filling the form
        cy.get('input[id=login_username]').type('yosi')
        cy.get('input[id=login_password]').type('apassword')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('logging in with an existing user but with incorrect password', () => {

        // filling the form
        cy.get('input[id=login_username]').type(instructor.username)
        cy.get('input[id=login_password]').type(instructor.password + 'yoyo')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('logging in and then trying to get back to the login page', () => {
        // filling the form
        cy.get('input[id=login_username]').type(instructor.username)
        cy.get('input[id=login_password]').type(instructor.password)

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')

        // typing in the url
        cy.visit('/user/login')

        // checking that the user got redirected to the home page
        cy.url().should('include', '/user/home')

        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('logging in when someone just changed that password to that account', () => {
        // logging in as a supervisor
        // filling the form
        cy.get('input[id=login_username]').type(supervisor.username)
        cy.get('input[id=login_password]').type(supervisor.password)

        // submitting
        cy.get('[id=login_button]').click()

        cy.get(`#user_collapse_button_${instructor.username}`).click() // opening the cell of the instructor

        // changing the password
        // opening the change password dialog
        cy.get(`#change_password_${instructor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type('IdkAnymore1234')
        cy.get('#change_password_confirm').type('IdkAnymore1234')

        cy.get('#change_password_submit_button').click() // submitting

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password);
        cy.get('#auth_submit_button').click()

        // logging out
        cy.get('[id=logout_button]').click()

        // filling the form
        cy.get('input[id=login_username]').type(instructor.username)
        cy.get('input[id=login_password]').type(instructor.password)

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