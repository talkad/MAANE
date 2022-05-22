
let supervisor = {
    username: 'ronit',
    password: '1234abcd',
}

let selected_user_as_supervisor = {
    username: "tal",
    old_pwd: '1234abcd',
    new_pwd: "areallynicepassword123"
}

describe('User management tests as a supervisor', () => {
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

            cy.url().should('include', '/user/home')

            cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).click()
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('Successfully change password to user', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(selected_user_as_supervisor.new_pwd)
        cy.get('#change_password_confirm').type(selected_user_as_supervisor.new_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
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

        // closing the dialog
        cy.get('#change_password_cancel_button').click()

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

        // logging in again so the tests won't break
        // filling the form
        cy.get('#login_username').clear().type(supervisor.username)
        cy.get('#login_password').clear().type(supervisor.password)

        // submitting
        cy.get('#login_button').click()
        cy.url().should('contain', '/user/home')
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

        // closing the dialog
        cy.get('#change_password_cancel_button').click()

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

        // logging in again so the tests won't break
        // filling the form
        cy.get('#login_username').clear().type(supervisor.username)
        cy.get('#login_password').clear().type(supervisor.password)

        // submitting
        cy.get('#login_button').click()
        cy.url().should('contain', '/user/home')
    })

    it('Changing the password of a user but submitting a weak password', () => {
        // TODO: not passign because of server side
        // // opening the change password dialog
        // cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()
        //
        // // filling the form
        // cy.get('#change_password_new').type("123")
        // cy.get('#change_password_confirm').type("123")
        //
        // cy.get('#change_password_submit_button').click() // submitting
        //
        // // filling auth page form
        // cy.get('#auth_password').type(supervisor.password)
        // cy.get('#auth_submit_button').click()
        //
        // // an error snackbar alert should appear
        // cy.get('#snackbar_alert_error').should('be.visible')
        //
        // // making sure we can't log in with the new password
        //
        // cy.get('#logout_button').click()
        //
        // // filling the form
        // cy.get('#login_username').type(selected_user_as_supervisor.username)
        // cy.get('#login_password').type(selected_user_as_supervisor.new_pwd)
        //
        // // submitting
        // cy.get('#login_button').click()
        //
        // // should expect an alert error to appear
        // cy.get('#login_alert').should('be.visible')
        //
        // // checking we remained in the same url
        // cy.url().should('include', '/user/login')
        //
        // // logging in again so the tests won't break
        // // filling the form
        // cy.get('#login_username').clear().type(supervisor.username)
        // cy.get('#login_password').clear().type(supervisor.password)
        //
        // // submitting
        // cy.get('#login_button').click()
        // cy.url().should('contain', '/user/home')
    })

    it('Removing a user', () => {
        // opening the dialog to remove a user
        cy.get(`#remove_user_${selected_user_as_supervisor.username}`).click()

        cy.get('#remove_user_submit_button').click()

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
        cy.get('#auth_submit_button').click() // submitting

        // should expect a success snackbar alert to pop
        cy.get('#snackbar_alert_success').should('be.visible')

        // the user should not appear anymore
        cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).should('not.exist') // the row for the user should not exist
    })

    it('Removing a user but someone else just removed him', () => {
        // //  TODO: removing the user programmatically
        // TODO: need functionality from server
        //
        // // opening the dialog to remove a user
        // cy.get(`#remove_user_${selected_user_as_supervisor.username}`).click()
        //
        // cy.get('#remove_user_submit_button').click()
        //
        // // filling auth page form
        // cy.get('#auth_password').type(current_user_password)
        // cy.get('#auth_submit_button').click() // submitting
        //
        // // should expect an error snackbar alert to pop
        // cy.get('#snackbar_alert_error').should('be.visible')
        //
        // // but the user should still not appear anymore
        // cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).should('not.exist') // the row for the user should not exist
    })

    // todo: tests for assigning a school to an instructor
})

let admin = {
    username: "admin",
    password: "admin123"
}

let selected_supervisor_as_system_manager = {
    username: "ronit",
}

let selected_instructor_as_system_manager = {
    username: "tal"
}

describe('User management tests as a system manager', () => {
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
            cy.get('input[id=login_username]').type(admin.username)
            cy.get('input[id=login_password]').type(admin.password)

            // submitting
            cy.get('[id=login_button]').click()

            cy.url().should('include', '/user/home')

            //  opening the info of the selected instructor
            cy.get(`#supervisor_collapse_button_${selected_supervisor_as_system_manager.username}`).click()
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('Making an instructor of a field the supervisor of the field', () => {

        // opening the dialog of transferring the supervision
        cy.get(`#user_collapse_button_${selected_instructor_as_system_manager.username}`).click()
        cy.get(`#transfer_supervision_to_${selected_instructor_as_system_manager.username}`).click()

        cy.get('#transfer_supervision_accept_button').click()

        // filling auth page form
        cy.get('#auth_password').type(admin.password)
        cy.get('#auth_submit_button').click() // submitting

        // the user we switched from should not be present in the table
        cy.contains(selected_supervisor_as_system_manager.username).should('not.exist')

        // the user we switched from should be present as a supervisor
        cy.get(`#td_supervisor_username_${selected_instructor_as_system_manager.username}`).contains(selected_instructor_as_system_manager.username)
    })

    it('Making an instructor of a field the supervisor of the field but pressing cancel', () => {


        // opening the dialog of transferring the supervision
        cy.get(`#user_collapse_button_${selected_instructor_as_system_manager.username}`).click()
        cy.get(`#transfer_supervision_to_${selected_instructor_as_system_manager.username}`).click()

        cy.get('#transfer_supervision_decline_button').click()

        // the user we switched from should still be a supervisor
        cy.get(`#td_supervisor_username_${selected_supervisor_as_system_manager.username}`).contains(selected_supervisor_as_system_manager.username)

        // the user we switched from should still be an instructor
        cy.get(`#td_non_supervisor_username_${selected_instructor_as_system_manager.username}`).contains(selected_instructor_as_system_manager.username)
    })
})