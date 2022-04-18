
let user_to_register = {
    username: "anakin_the_slayer",
    password: "darth",
    firstName: "anakin",
    lastName: "skywalker",
    email: "anakinsky@gmail.com",
    phoneNumber: "054-1234567",
    city: "tatooine",
}

describe('Register users tests as supervisor', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor
        cy.visit('/user/registerUsers')
    })

    it('registering an instructor only with mandatory fields', () => {

        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('#snackbar_alert_success')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מדריכ/ה"
        cy.find(user_to_register.username)
        cy.find(user_to_register.firstName)
        cy.find(user_to_register.lastName)
        cy.get(`#td_role_${user_to_register.username}`).should('contain', role_choice)
    })

    it('registering a general supervisor only with mandatory fields', () => {

        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="GENERAL_SUPERVISOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('#snackbar_alert_success')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מפקח/ת כללי/ת"
        cy.find(user_to_register.username)
        cy.find(user_to_register.firstName)
        cy.find(user_to_register.lastName)
        cy.get(`#td_role_${user_to_register.username}`).should('contain', role_choice)
    })

    it('registering a user with a mandatory field missing', () => {
        // filling the form
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="GENERAL_SUPERVISOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.contains(user_to_register.firstName).should('not.exist')
    })

    it('registering two users with the same username', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('#snackbar_alert_success')

        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(`${user_to_register.password}2`)
        cy.get('#register_first_name').type(`${user_to_register.firstName}2`)
        cy.get('#register_last_name').type(`${user_to_register.lastName}2`)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_alert')

        // going back to the table of the users and making sure the user is not appearing there twice
        cy.get('#register_finish_button').click()

        cy.get(`contains(${user_to_register.firstName})`).should('have.length', 1); // TODO: make sure this works
    })

    it('registering a user with all the fields', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)
        cy.get('#register_email').type(user_to_register.email)
        cy.get('#register_phone_number').type(user_to_register.phoneNumber)
        cy.get('#register_city').type(user_to_register.city)

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('#snackbar_alert_success')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מדריכ/ה"
        cy.find(user_to_register.firstName)
        cy.find(user_to_register.lastName)
        cy.get(`#td_role_${user_to_register.username}`).should('contain', role_choice)
        cy.find(user_to_register.email)
        cy.find(user_to_register.phoneNumber)
        cy.find(user_to_register.city)
    })

    it('registering a user with an invalid email address', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)
        cy.get('#register_email').type("anakingmail.com")

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.contains(user_to_register.firstName).should('not.exist')
    })

    it('registering a user with an invalid phone number', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('#register_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.contains(user_to_register.firstName).should('not.exist')
    })
})

describe('Register users tests as admin', () => {
    beforeEach(() => {
        //TODO: format the db and log in as an admin
    })

    it('will pass', () => {
        expect(true).to.equal(true)
    })
})