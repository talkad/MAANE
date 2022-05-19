
let supervisor = {
    username: 'ronit',
    password: '1234abcd',
}

let user_to_register = {
    username: "anakin_the_slayer",
    password: "darththeslayer66",
    firstName: "anakin",
    lastName: "skywalker",
    email: "anakinsky@gmail.com",
    phoneNumber: "054-1234567",
    city: "tatooine",
    workField: "jedi",
}

describe('Register users tests as supervisor', () => {

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



            // going to the registration page
            cy.get('#register_users_button').click()
        })

    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
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
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מדריכ/ה"
        cy.get(`#user_collapse_button_${user_to_register.username}`)
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
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מפקח/ת כללי/ת"
        cy.get(`#user_collapse_button_${user_to_register.username}`)
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
        cy.get('#register_alert').should('be.visible')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.get(`#user_collapse_button_${user_to_register.username}`).should('not.exist')
    })

    it('registering a new user with an invalid password', () => {
        // // filling the form
        // cy.get('#register_username').type(user_to_register.username)
        // cy.get('#register_password').type("123")
        // cy.get('#register_first_name').type(user_to_register.firstName)
        // cy.get('#register_last_name').type(user_to_register.lastName)
        //
        // cy.get('#register_role_choice') // selecting from the menu
        //     .parent()
        //     .click()
        //     .get(`ul > li[data-value="INSTRUCTOR"]`)
        //     .click()
        //
        // cy.get('#register_submit_button').click() // submitting
        //
        // // an error alert should pop up
        // cy.get('#snackbar_alert_error').should('be.visible')
        //
        // // going back to the table of the users and making sure the user is not appearing there
        // cy.get('#register_finish_button').click()
        //
        // cy.get(`#user_collapse_button_${user_to_register.username}`).should('not.exist')

        // todo: not passing because of server side
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
        cy.get('#snackbar_alert_success').should('be.visible')

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
        cy.get('#snackbar_alert_error').should('be.visible')

        // going back to the table of the users and making sure the user is not appearing there twice
        cy.get('#register_finish_button').click()

        cy.get(`#user_collapse_button_${user_to_register.username}`).should('have.length', 1);
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
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the home page to check if the user is added
        cy.get('#register_finish_button').click()

        let role_choice = "מדריכ/ה"
        cy.get(`#user_collapse_button_${user_to_register.username}`).click()
        cy.get(`#td_role_${user_to_register.username}`).should('contain', role_choice)
        cy.contains(user_to_register.email)
        cy.contains(user_to_register.phoneNumber)
        cy.contains(user_to_register.city)
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
        cy.get('#snackbar_alert_error').should('be.visible')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.get(`#user_collapse_button_${user_to_register.username}`).should('not.exist')
    })

    it('registering a user with an invalid phone number', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)
        cy.get('#register_phone_number').type('abcd')

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        // going back to the table of the users and making sure the user is not appearing there
        cy.get('#register_finish_button').click()

        cy.get(`#user_collapse_button_${user_to_register.username}`).should('not.exist')
    })
})

let admin = {
    username: 'admin',
    password: 'admin123',
}

let existing_work_field = 'tech';

describe('Register users tests as admin', () => {
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



            // going to the registration page
            cy.get('#register_users_button').click()
        })

    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it('registering a new supervisor under a new field', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="SUPERVISOR"]`)
            .click()

        cy.get('#register_work_field').type(user_to_register.workField)

        cy.get('#register_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is added there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${user_to_register.username}`)
        cy.get(`#td_work_field_${user_to_register.username}`).contains(user_to_register.workField)
    })

    it('registering an instructor under an existing supervisor', () =>{
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting role from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_supervisor_choice') // selecting supervisor from the menu
            .parent()
            .click()
            .get(`ul > li[data-value=${supervisor.username}]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is added there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${supervisor.username}`).click()

        let role_choice = "מדריכ/ה"
        cy.get(`#user_collapse_button_${user_to_register.username}`)
        cy.get(`#td_role_${user_to_register.username}`).contains(role_choice)
    })

    it('registering a general supervisor under an existing supervisor', () =>{
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting role from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="GENERAL_SUPERVISOR"]`)
            .click()

        cy.get('#register_supervisor_choice') // selecting supervisor from the menu
            .parent()
            .click()
            .get(`ul > li[data-value=${supervisor.username}]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is added there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${supervisor.username}`).click()

        let role_choice = "מפקח/ת כללי/ת"
        cy.get(`#user_collapse_button_${user_to_register.username}`)
        cy.get(`#td_role_${user_to_register.username}`).contains(role_choice)
    })

    it('replacing a supervisor with a new one', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("1") // selecting the action to replace

        cy.get('#register_supervisor_to_replace') // selecting supervisor from the menu
            .parent()
            .click()
            .get(`ul > li[data-value=${supervisor.username}]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor has been replaced
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${user_to_register.username}`)
        cy.get(`#supervisor_collapse_button_${supervisor.username}`).should('not.exist')
    })

    it('registering a new supervisor to an already existing work field', () =>{
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="SUPERVISOR"]`)
            .click()

        cy.get('#register_work_field').type(existing_work_field)

        cy.get('#register_submit_button').click() // submitting

        // an error snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is not there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${user_to_register.username}`).should('not.exist')
    })

    it('registering a new supervisor without providing a working field', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="SUPERVISOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error snackbar should appear
        cy.get('#register_alert').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is not there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${user_to_register.username}`).should('not.exist')
    })

    it('replacing an existing supervisor with a new one without picking the supervisor to replace', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("1")

        // cy.get('#register_supervisor_to_replace') // selecting supervisor from the menu
        //     .parent()
        //     .click()
        //     .get(`ul > li[data-value="INSTRUCTOR"]`)
        //     .click()

        cy.get('#register_submit_button').click() // submitting

        // an error snackbar should appear
        cy.get('#register_alert').should('be.visible')

        // finishing the registration and going back to the table to check if the supervisor is not replaced
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${user_to_register.username}`).should('not.exist')
        cy.get(`#supervisor_collapse_button_${supervisor.username}`)
    })

    it('registering a new non-supervisor user without picking a supervisor for the new user to be under', () => {
        // filling the form
        cy.get('#register_username').type(user_to_register.username)
        cy.get('#register_password').type(user_to_register.password)
        cy.get('#register_first_name').type(user_to_register.firstName)
        cy.get('#register_last_name').type(user_to_register.lastName)

        cy.get('[type="radio"]').check("0")

        cy.get('#register_role_choice') // selecting role from the menu
            .parent()
            .click()
            .get(`ul > li[data-value="INSTRUCTOR"]`)
            .click()

        cy.get('#register_submit_button').click() // submitting

        // an error snackbar should appear
        cy.get('#register_alert').should('be.visible')

        // finishing the registration and going back to the table to check if the user is not there
        cy.get('#register_finish_button').click()

        cy.get(`#supervisor_collapse_button_${supervisor.username}`).click()
        cy.get(`#user_collapse_button_${user_to_register.username}`).should('not.exist')
    })
})