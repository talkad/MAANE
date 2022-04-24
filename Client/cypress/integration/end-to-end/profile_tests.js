
let info_to_edit_to = {
    firstName: "obi-wan",
    lastName: "kenobi",
    email: "masterK@jedi.com",
    phoneNumber: "052-1313628",
    city: "stewjon",
}

describe('Profile page tests for editing info', () => {
    beforeEach(() => {
        //TODO: format the db and log in
        cy.visit('/user/profile')

        // pressing the edit button
        cy.get('#profile_edit_button').click()
    })

    it('Successfully editing info', () => {
        // filling the form
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)
        cy.get('#profile_edit_email').type(info_to_edit_to.email)
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)
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
        let firstName = ''
        let lastName = ''
        let email = ''
        let phoneNumber = ''
        let city = ''

        // getting the previous data
        cy.get('#profile_edit_first_name')
            .invoke('val')
            .then(text => firstName = text)

        cy.get('#profile_edit_last_name')
            .invoke('val')
            .then(text => lastName = text)

        cy.get('#profile_edit_email')
            .invoke('val')
            .then(text => email = text)

        cy.get('#profile_edit_phone_number')
            .invoke('val')
            .then(text => phoneNumber = text)

        cy.get('#profile_edit_city')
            .invoke('val')
            .then(text => city = text)

        // filling the form but leaving the first name empty
        cy.get('#profile_edit_first_name').type('')
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)
        cy.get('#profile_edit_email').type(info_to_edit_to.email)
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('be.visible')

        // upon reload everything should remain as it had been
        cy.get('#profile_edit_first_name').should('have.value', firstName)
        cy.get('#profile_edit_last_name').should('have.value', lastName)
        cy.get('#profile_edit_email').should('have.value', email)
        cy.get('#profile_edit_phone_number').should('have.value', phoneNumber)
        cy.get('#profile_edit_city').should('have.value', city)
    })

    it('Editing but submitting with the last name field empty', () => {
        let firstName = ''
        let lastName = ''
        let email = ''
        let phoneNumber = ''
        let city = ''

        // getting the previous data
        cy.get('#profile_edit_first_name')
            .invoke('val')
            .then(text => firstName = text)

        cy.get('#profile_edit_last_name')
            .invoke('val')
            .then(text => lastName = text)

        cy.get('#profile_edit_email')
            .invoke('val')
            .then(text => email = text)

        cy.get('#profile_edit_phone_number')
            .invoke('val')
            .then(text => phoneNumber = text)

        cy.get('#profile_edit_city')
            .invoke('val')
            .then(text => city = text)

        // filling the form but leaving the last name empty
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)
        cy.get('#profile_edit_last_name').type('')
        cy.get('#profile_edit_email').type(info_to_edit_to.email)
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('be.visible')

        // upon reload everything should remain as it had been
        cy.get('#profile_edit_first_name').should('have.value', firstName)
        cy.get('#profile_edit_last_name').should('have.value', lastName)
        cy.get('#profile_edit_email').should('have.value', email)
        cy.get('#profile_edit_phone_number').should('have.value', phoneNumber)
        cy.get('#profile_edit_city').should('have.value', city)
    })

    it('Editing but submitting with an invalid email address', () => {
        let firstName = ''
        let lastName = ''
        let email = ''
        let phoneNumber = ''
        let city = ''

        // getting the previous data
        cy.get('#profile_edit_first_name')
            .invoke('val')
            .then(text => firstName = text)

        cy.get('#profile_edit_last_name')
            .invoke('val')
            .then(text => lastName = text)

        cy.get('#profile_edit_email')
            .invoke('val')
            .then(text => email = text)

        cy.get('#profile_edit_phone_number')
            .invoke('val')
            .then(text => phoneNumber = text)

        cy.get('#profile_edit_city')
            .invoke('val')
            .then(text => city = text)

        // filling the form but entering an invalid email address
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)
        cy.get('#profile_edit_email').type('masterkgmailcom')
        cy.get('#profile_edit_phone_number').type(info_to_edit_to.phoneNumber)
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('be.visible')

        // upon reload everything should remain as it had been
        cy.get('#profile_edit_first_name').should('have.value', firstName)
        cy.get('#profile_edit_last_name').should('have.value', lastName)
        cy.get('#profile_edit_email').should('have.value', email)
        cy.get('#profile_edit_phone_number').should('have.value', phoneNumber)
        cy.get('#profile_edit_city').should('have.value', city)
    })

    it('Editing but submitting with an invalid phone number', () => {
        let firstName = ''
        let lastName = ''
        let email = ''
        let phoneNumber = ''
        let city = ''

        // getting the previous data
        cy.get('#profile_edit_first_name')
            .invoke('val')
            .then(text => firstName = text)

        cy.get('#profile_edit_last_name')
            .invoke('val')
            .then(text => lastName = text)

        cy.get('#profile_edit_email')
            .invoke('val')
            .then(text => email = text)

        cy.get('#profile_edit_phone_number')
            .invoke('val')
            .then(text => phoneNumber = text)

        cy.get('#profile_edit_city')
            .invoke('val')
            .then(text => city = text)

        // filling the form but entering an invalid email address
        cy.get('#profile_edit_first_name').type(info_to_edit_to.firstName)
        cy.get('#profile_edit_last_name').type(info_to_edit_to.lastName)
        cy.get('#profile_edit_email').type(info_to_edit_to.email)
        cy.get('#profile_edit_phone_number').type('a12')
        cy.get('#profile_edit_city').type(info_to_edit_to.city)

        cy.get('#profile_edit_submit').click() // submitting

        // an error alert should pop up
        cy.get('#profile_edit_alert').should('be.visible')

        // upon reload everything should remain as it had been
        cy.get('#profile_edit_first_name').should('have.value', firstName)
        cy.get('#profile_edit_last_name').should('have.value', lastName)
        cy.get('#profile_edit_email').should('have.value', email)
        cy.get('#profile_edit_phone_number').should('have.value', phoneNumber)
        cy.get('#profile_edit_city').should('have.value', city)
    })
})

let password_change_info = {
    current_pwd: "kmaster123", //TODO: change to the real one
    new_pwd: "anislayerB)",
    confirm_pwd: "anislayerV)"
}

describe('Profile page tests for security features', () => {
    beforeEach(() => {
        //TODO: format the db and log in
        cy.visit('/user/profile')

        // pressing the sucurity tab
        cy.get('#vertical-tab-1').click()
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
        cy.get('logout_button').click()

        // filling the login form
        cy.get('#login_username').type("kenobi") // TODO: get the real one
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        cy.url().should('include', '/user/home') // should get to the home screen aka logged in
    })

    it('Changing the password but the current password is wrong', () => {
        // filling the form
        cy.get('#change_password_current').type('123')
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('logout_button').click()

        // filling the login form
        cy.get('#login_username').type("kenobi") // TODO: get the real one
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')
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
        cy.get('logout_button').click()

        // filling the login form
        cy.get('#login_username').type("kenobi") // TODO: get the real one
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')
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
        cy.get('logout_button').click()

        // filling the form
        cy.get('#login_username').type("kenobi") // TODO: get the real one
        cy.get('#login_password').type("123")

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')
    })

    it("Changing the password while a supervisor changes the password to the user", () => {
        // TODO: send the request through cy.request to change the password

        // filling the form
        cy.get('#change_password_current').type(password_change_info.current_pwd)
        cy.get('#change_password_new').type(password_change_info.new_pwd)
        cy.get('#change_password_confirm').type(password_change_info.confirm_pwd)

        cy.get('#change_password_submit_button').click() // submitting

        // an error snackbar alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // logging out and making sure we can't log in with the new password
        cy.get('logout_button').click()

        // filling the login form
        cy.get('#login_username').type("kenobi") // TODO: get the real one
        cy.get('#login_password').type(password_change_info.new_pwd)

        // submitting
        cy.get('#login_button').click()

        // should expect an alert error to appear
        cy.get('#login_alert').should('be.visible')
    })
})