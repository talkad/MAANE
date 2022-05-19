
let supervisor = {
    username: 'ronit',
    password: '1234abcd',
}

let admin = {
    username: 'admin',
    password: 'admin123',
}

let selected_user_to_delete = {
    username: "tal"
}

describe('Password authentication tests', () => {
    beforeEach(() => {
        cy.request({
            method: 'POST',
            url: "http://localhost:8080/data/resetDB",
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(() => {

            // visiting the login page
            cy.visit('/user/login')

            // filling the login form
            cy.get('input[id=login_username]').type(supervisor.username)
            cy.get('input[id=login_password]').type(supervisor.password)

            // submitting
            cy.get('[id=login_button]').click()


            // the tests for the password authentication will go through the action of deleting a user
            cy.get(`#user_collapse_button_${selected_user_to_delete.username}`).click() // opening the info of the user
            cy.get(`#remove_user_${selected_user_to_delete.username}`).click() // opening the dialog
            cy.get('#remove_user_submit_button').click() // confirming the action and going to the auth page
        })

    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('Successful authentication', () => {
        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
        cy.get('#auth_submit_button').click() // submitting

        // the user should not appear anymore
        cy.get(`#user_collapse_button_${selected_user_to_delete.username}`).should('not.exist') // the row for the user should not exist
    })

    it('Unsuccessful authentication', () => {
        // filling auth page form
        cy.get('#auth_password').type(supervisor.password + "123")
        cy.get('#auth_submit_button').click() // submitting

        // an error alert should appear
        cy.get(`#auth_alert`).should('be.visible')

        // going back to the home page and the user should still be there
        cy.get('#auth_cancel_button').click() // cancelling

        cy.contains(selected_user_to_delete.username).should('exist')
    })

    it('Pressing cancel', () => {
        cy.get('#auth_cancel_button').click() // cancelling

        // should go back and still find the user there
        cy.contains(selected_user_to_delete.username).should('exist')
    })

    it('Password of supervisor changed by system manager just before authing', () => {
        cy.get('#auth_cancel_button').click() // cancelling

        // logging out
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')

        // logging in as admin
        // filling the login form
        cy.get('input[id=login_username]').type(admin.username)
        cy.get('input[id=login_password]').type(admin.password)

        // submitting
        cy.get('[id=login_button]').click()

        cy.get(`#supervisor_collapse_button_${supervisor.username}`).click()

        // opening the change password dialog
        cy.get(`#change_password_${supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(supervisor.password + "123")
        cy.get('#change_password_confirm').type(supervisor.password + "123")

        cy.get('#change_password_submit_button').click() // submitting

        // filling auth page form
        cy.get('#auth_password').type(admin.password)
        cy.get('#auth_submit_button').click()

        // logging out
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')

        // filling the login form
        cy.get('input[id=login_username]').type(supervisor.username)
        cy.get('input[id=login_password]').type(supervisor.password + "123")

        // submitting
        cy.get('[id=login_button]').click()


        // the tests for the password authentication will go through the action of deleting a user
        cy.get(`#user_collapse_button_${selected_user_to_delete.username}`).click() // opening the info of the user
        cy.get(`#remove_user_${selected_user_to_delete.username}`).click() // opening the dialog
        cy.get('#remove_user_submit_button').click() // confirming the action and going to the auth page

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
        cy.get('#auth_submit_button').click()

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
        cy.get('#auth_submit_button').click() // submitting

        // an error alert should appear
        cy.get(`#auth_alert`).should('be.visible')

        // going back to the home page and the user should still be there
        cy.get('#auth_cancel_button').click() // cancelling

        cy.contains(selected_user_to_delete.username).should('exist')
    })
})