
let selected_user_to_delete = {
    username: "Lightsong"
}

let password_of_current_user

describe('Password authentication tests', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor

        cy.visit('/user/home')

        // the tests for the password authentication will go through the action of deleting a user
        cy.get(`#user_collapse_button_${selected_user_to_delete.username}`).click() // opening the info of the user
        cy.get(`#remove_user_${selected_user_to_delete.username}`).click() // opening the dialog
        cy.get('#remove_user_submit_button').click() // confirming the action and going to the auth page
    })

    it('Successful authentication', () => {
        // filling auth page form
        cy.get('#auth_password').type(password_of_current_user)
        cy.get('#auth_submit_button').click() // submitting

        // the user should not appear anymore
        cy.get(`#user_collapse_button_${selected_user_to_delete.username}`).should('not.exist') // the row for the user should not exist
    })

    it('Unsuccessful authentication', () => {
        // filling auth page form
        cy.get('#auth_password').type(password_of_current_user + "123")
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
        // TODO: programmatically change the password of the current user

        // filling auth page form
        cy.get('#auth_password').type(password_of_current_user)
        cy.get('#auth_submit_button').click() // submitting

        // an error alert should appear
        cy.get(`#auth_alert`).should('be.visible')

        // going back to the home page and the user should still be there
        cy.get('#auth_cancel_button').click() // cancelling

        cy.contains(selected_user_to_delete.username).should('exist')
    })
})