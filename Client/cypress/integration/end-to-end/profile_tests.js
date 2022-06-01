
let user = {
    username: "tal",
    password: "1234abcd",
}

let info_to_edit_to = {
    firstName: "obi-wan",
    lastName: "kenobi",
    email: "masterK@jedi.com",
    phoneNumber: "052-1313628",
    city: "stewjon",
}

let prev_info = {
    firstName: "tal",
    lastName: "kad",
    email: "tal@gmail.com",
    phoneNumber: "055-555-5555",
    city: "",
}

describe('Profile page tests for editing info', () => {

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
            cy.get('input[id=login_username]').type(user.username)
            cy.get('input[id=login_password]').type(user.password)

            // submitting
            cy.get('[id=login_button]').click()

            cy.url().should('include', '/user/home')

            cy.visit('/user/profile')

            // pressing the edit button
            cy.get('#profile_edit_button').click()
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('Successfully editing info', () => {
        // filling the form
        cy.get('#profile_edit_first_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_last_name').clear()
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)

        cy.get('#profile_edit_email').clear()
        cy.get('#profile_edit_email').type(info_to_edit_to.email)

        cy.get('#profile_edit_phone_number').clear()
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)

        cy.get('#profile_edit_city').clear()
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // a success snackbar alert should pop up
        cy.get('#snackbar_alert_success').should('be.visible')

        // now upon refreshing the page all the info should remain
        cy.reload()

        cy.get('#profile_edit_first_name').should('have.value', info_to_edit_to.firstName)
        cy.get('#profile_edit_last_name').should('have.value', info_to_edit_to.lastName)
        cy.get('#profile_edit_email').should('have.value', info_to_edit_to.email)
        cy.get('#profile_edit_phone_number').should('have.value', info_to_edit_to.phoneNumber)
        cy.get('#profile_edit_city').should('have.value', info_to_edit_to.city)
    })

    it('Editing but submitting with the first name field empty', () => {

        // filling the form with the first name empty
        cy.get('#profile_edit_first_name').clear()

        cy.get('#profile_edit_last_name').clear()
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)

        cy.get('#profile_edit_email').clear()
        cy.get('#profile_edit_email').type(info_to_edit_to.email)

        cy.get('#profile_edit_phone_number').clear()
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)

        cy.get('#profile_edit_city').clear()
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('exist')

        // upon reload everything should remain as it had been
        cy.reload()

        cy.get('#profile_edit_first_name').should('have.value', prev_info.firstName)
        cy.get('#profile_edit_last_name').should('have.value', prev_info.lastName)
        cy.get('#profile_edit_email').should('have.value', prev_info.email)
        cy.get('#profile_edit_phone_number').should('have.value', prev_info.phoneNumber)
        cy.get('#profile_edit_city').should('have.value', prev_info.city)
    })

    it('Editing but submitting with the last name field empty', () => {

        // filling the form but leaving the last name empty
        cy.get('#profile_edit_first_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_last_name').clear()

        cy.get('#profile_edit_email').clear()
        cy.get('#profile_edit_email').type(info_to_edit_to.email)

        cy.get('#profile_edit_phone_number').clear()
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)

        cy.get('#profile_edit_city').clear()
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('exist')

        cy.reload()

        cy.get('#profile_edit_first_name').should('have.value', prev_info.firstName)
        cy.get('#profile_edit_last_name').should('have.value', prev_info.lastName)
        cy.get('#profile_edit_email').should('have.value', prev_info.email)
        cy.get('#profile_edit_phone_number').should('have.value', prev_info.phoneNumber)
        cy.get('#profile_edit_city').should('have.value', prev_info.city)
    })

    it('Editing but submitting with an invalid email address', () => {

        // filling the form but entering an invalid email address
        cy.get('#profile_edit_first_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_last_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_email').clear()
        cy.get('#profile_edit_email').type('hellotheregmailcom')

        cy.get('#profile_edit_phone_number').clear()
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)

        cy.get('#profile_edit_city').clear()
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('exist')

        // upon reload everything should remain as it had been
        cy.reload()

        cy.get('#profile_edit_first_name').should('have.value', prev_info.firstName)
        cy.get('#profile_edit_last_name').should('have.value', prev_info.lastName)
        cy.get('#profile_edit_email').should('have.value', prev_info.email)
        cy.get('#profile_edit_phone_number').should('have.value', prev_info.phoneNumber)
        cy.get('#profile_edit_city').should('have.value', prev_info.city)
    })

    it('Editing but submitting with an invalid phone number', () => {

        // filling the form but entering an invalid email address
        cy.get('#profile_edit_first_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_last_name').clear()
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)

        cy.get('#profile_edit_email').clear()
        cy.get('#profile_edit_email').type(info_to_edit_to.email)

        cy.get('#profile_edit_phone_number').clear()
        cy.get('#profile_edit_phone_number').type('aaa')

        cy.get('#profile_edit_city').clear()
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('exist')

        // upon reload everything should remain as it had been
        cy.reload()

        cy.get('#profile_edit_first_name').should('have.value', prev_info.firstName)
        cy.get('#profile_edit_last_name').should('have.value', prev_info.lastName)
        cy.get('#profile_edit_email').should('have.value', prev_info.email)
        cy.get('#profile_edit_phone_number').should('have.value', prev_info.phoneNumber)
        cy.get('#profile_edit_city').should('have.value', prev_info.city)
    })
})

let password_change_info = {
    current_pwd: user.password,
    new_pwd: "anislayerB)66",
    confirm_pwd: "anislayerB)66"
}

let supervisor = {
    username: 'ronit',
    password: '1234abcd'
}

describe('Profile page tests for security features', () => {
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
            cy.get('input[id=login_username]').type(user.username)
            cy.get('input[id=login_password]').type(user.password)

            // submitting
            cy.get('[id=login_button]').click()

            cy.url().should('include', '/user/home')

            cy.visit('/user/profile')

            // pressing the sucurity tab
            cy.get('#vertical-tab-1').click()
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('Successfully changing the password', () => {
        // filling the form
        cy.get('#change_password_current').type(password_change_info.current_pwd)
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // a success snackbar alert should pop up
        cy.get('#snackbar_alert_success').should('be.visible')

        // logging out and checking if we can log in with the new password
        cy.get('#logout_button').click()

        // filling the login form
        cy.get('#login_username').type(user.username)
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        cy.url().should('include', '/user/home') // should get to the home screen aka logged in
    })

    it('Changing the password but the current password is wrong', () => {
        // filling the form
        cy.get('#change_password_current').type(password_change_info.current_pwd + "1")
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('#logout_button').click()

        // filling the login form
        cy.get('#login_username').type(user.username)
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // logging in again so the next tests in line will work
        cy.get('#login_username').clear()
        cy.get('#login_username').type(user.username)

        cy.get('#login_password').clear()
        cy.get('#login_password').type(user.password)

        // submitting
        cy.get('#login_button').click()
    })

    it('Changing the password but the new and confirmed password does not match', () => {
        // filling the form
        cy.get('#change_password_current').type(password_change_info.current_pwd)
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd + "123412341")

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('#logout_button').click()

        // filling the login form
        cy.get('#login_username').type(user.username)
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // logging in again so the next tests in line will work
        cy.get('#login_username').clear()
        cy.get('#login_username').type(user.username)

        cy.get('#login_password').clear()
        cy.get('#login_password').type(user.password)

        // submitting
        cy.get('#login_button').click()
    })

    it('The new password is invalid', () => {
        // filling the form with a weak password
        cy.get('#change_password_current').type(password_change_info.current_pwd)
        cy.get('#change_password_new').type("123")
        cy.get('#change_password_confirm').type("123")

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('#logout_button').click()

        // filling the form
        cy.get('#login_username').type(user.username)
        cy.get('#login_password').type("123")

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // logging in again so the next tests in line will work
        cy.get('#login_username').clear()
        cy.get('#login_username').type(user.username)

        cy.get('#login_password').clear()
        cy.get('#login_password').type(user.password)

        // submitting
        cy.get('#login_button').click()
    })

    it("Changing the password while a supervisor changes the password to the user", () => {
        // logging out
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')

        // logging in as a supervisor
        // filling the login form
        cy.get('input[id=login_username]').type(supervisor.username)
        cy.get('input[id=login_password]').type(supervisor.password)

        // submitting
        cy.get('[id=login_button]').click()

        // opening the change password dialog
        cy.get(`#user_collapse_button_${user.username}`).click()
        cy.get(`#change_password_${user.username}`).click()

        // filling the form
        cy.get('#change_password_new').type(user.password + "123")
        cy.get('#change_password_confirm').type(user.password + "123")

        cy.get('#change_password_submit_button').click() // submitting

        // filling auth page form
        cy.get('#auth_password').type(supervisor.password)
        cy.get('#auth_submit_button').click()

        // logging out
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')

        // filling the login form as the instructor
        cy.get('input[id=login_username]').type(user.username)
        cy.get('input[id=login_password]').type(user.password + "123")

        // submitting
        cy.get('[id=login_button]').click()

        cy.url().should('include', '/user/home')

        cy.visit('/user/profile')

        // pressing the sucurity tab
        cy.get('#vertical-tab-1').click()

        // filling the form
        cy.get('#change_password_current').type(password_change_info.current_pwd)
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('#logout_button').click()

        // filling the login form
        cy.get('#login_username').type(user.username)
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')

        // logging in again so the next tests in line will work
        cy.get('#login_username').clear()
        cy.get('#login_username').type(user.username)

        cy.get('#login_password').clear()
        cy.get('#login_password').type(user.password + "123")

        // submitting
        cy.get('#login_button').click()
    })
})