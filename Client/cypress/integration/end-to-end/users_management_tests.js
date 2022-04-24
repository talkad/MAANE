
let selected_user_as_supervisor = {
    username: "vivenna",
    new_pwd: "colors!"
}

let current_user_password = 'kaladsphantoms'

describe('User management test as a supervisor', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor
        cy.visit('/user/home')

        cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).click()
    })

    it('Successfully change password to user', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(selected_user_as_supervisor.new_pwd)
        cy.get('#change_password_confirm').type(selected_user_as_supervisor.new_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // filling auth page form
        cy.get('#auth_password').type(current_user_password)
        cy.get('#auth_submit_button').click()

        // should expect a success snackbar alert to pop
        cy.get('#snackbar_alert_success').should('be.visible')

        // logging out and logging in with the user we just changed the password to, to check it worked

        cy.get('#logout_button').click()

        // filling the form
        cy.get('#login_username').type(selected_user_as_supervisor.username)
        cy.get('#login_password').type(selected_user_as_supervisor.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')
    })

    it('Changing the password of a user but missing to fill a field', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(selected_user_as_supervisor.new_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // an alert should pop up
        cy.get('#change_password_alert').should('be.visible')

        // making sure we can't log in with the new password

        cy.get('#logout_button').click()

        // filling the form
        cy.get('#login_username').type(selected_user_as_supervisor.username)
        cy.get('#login_password').type(selected_user_as_supervisor.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('Changing the password for a user but the new and confirm fields does not match', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(selected_user_as_supervisor.new_pwd)
        cy.get('#change_password_confirm').type(selected_user_as_supervisor.new_pwd + "123")

        cy.get('#change_password_submit_button').click() // submitting

        // an alert should pop up
        cy.get('#change_password_alert').should('be.visible')

        // making sure we can't log in with the new password

        cy.get('#logout_button').click()

        // filling the form
        cy.get('#login_username').type(selected_user_as_supervisor.username)
        cy.get('#login_password').type(selected_user_as_supervisor.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('Changing the password of a user but submitting a weak password', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type("123")
        cy.get('#change_password_confirm').type("123")

        cy.get('#change_password_submit_button').click() // submitting

        // an alert should pop up
        cy.get('#change_password_alert').should('be.visible')

        // making sure we can't log in with the new password

        cy.get('#logout_button').click()

        // filling the form
        cy.get('#login_username').type(selected_user_as_supervisor.username)
        cy.get('#login_password').type(selected_user_as_supervisor.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/login')
    })

    it('Removing a user', () => {
        // opening the dialog to remove a user
        cy.get(`#remove_user_${selected_user_as_supervisor.username}`).click()

        cy.get('#remove_user_submit_button').click()

        // filling auth page form
        cy.get('#auth_password').type(current_user_password)
        cy.get('#auth_submit_button').click() // submitting

        // should expect a success snackbar alert to pop
        cy.get('#snackbar_alert_success').should('be.visible')

        // the user should not appear anymore
        cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).should('not.exist') // the row for the user should not exist
    })

    it('Removing a user but someone else just removed him', () => {
        //  TODO: removing the user programmatically

        // opening the dialog to remove a user
        cy.get(`#remove_user_${selected_user_as_supervisor.username}`).click()

        cy.get('#remove_user_submit_button').click()

        // filling auth page form
        cy.get('#auth_password').type(current_user_password)
        cy.get('#auth_submit_button').click() // submitting

        // should expect an error snackbar alert to pop
        cy.get('#snackbar_alert_error').should('be.visible')

        // but the user should still not appear anymore
        cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).should('not.exist') // the row for the user should not exist
    })
})

describe('User management test as a system manager', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a system manager
        cy.visit('/user/home')
    })
})