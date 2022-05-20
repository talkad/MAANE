
let supervisor = {
    username: 'ronit',
    password: '1234abcd'
}

let coordinator_to_add = {
    selected_school_id: '222222',
    first_name: "padme",
    last_name: "amidala",
    email: "iloveani@gmail.com",
    phone_number: "052-6735471"
}

// todo: fix the tests

describe('School management tests', () => {
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

            cy.visit('/user/schools')
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it.only('Adding a coordinator to a school', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id)
        cy.get('#search-schools').first().click()
        cy.get('#search_school_button').click()

        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button`)

        // filling the form
        cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type(coordinator_to_add.email)
        cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        cy.reload()

        // search up the school again
        cy.get('search_schools_text_field').type(coordinator_to_add.selected_school_id)
        cy.get('search_school_button').click()


        // checking if we can find the coordinator in the table under the selected school
        cy.contains(coordinator_to_add.first_name)
    })

    it('Adding a coordinator to a school with a missing field', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator_to_add.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type(coordinator_to_add.email)
        cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

        cy.get('add_coordinator_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#add_coordinator_alert').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid email address', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator_to_add.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type("iloveanigmailcom")
        cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error snackbar alert should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid phone number', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator_to_add.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type(coordinator_to_add.email)
        cy.get('#add_coordinator_phone_number').type("ab")

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error snackbar alert should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Removing a coordinator from a school', () => {
        // opening the removing dialog
        cy.get(`#remove_coordinator`).click()

        cy.get('#remove_coordinator_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // checking the coordinator was removed
        cy.contains(coordinator.first_name).should('not.exist')
    })

    it('Removing a coordinator from a school while someone else already removed the same coordinator', () => {
        // TODO: remove the selected coordinator programmatically

        // opening the removing dialog
        cy.get(`#remove_coordinator_${coordinator.email}`).click()

        cy.get('#remove_coordinator_submit_button').click() // submitting

        // an error snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // but the upon refresh the coordinator should be removed
        cy.reload()
        cy.get(`#school_collapse_button_${coordinator.selected_school_id}`).click()
        cy.contains(coordinator.first_name).should('not.exist')
    })


    // TODO: add to school while someone else adds a coordinator to a school
})