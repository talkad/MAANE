
let coordinator = {
    selected_school_id: 432, // TODO: change to the right id
    first_name: "padme",
    last_name: "amidala",
    email: "iloveani@gmail.com",
    phone_number: "052-6735471"
}


describe('School management tests', () => {
    beforeEach(() => {
        // todo: reformat the db and log in as a supervisor

        // visiting the school management page
        cy.visit('/user/schools')

        cy.get(`#school_collapse_button_${coordinator.selected_school_id}`).click()
    })

    it('Adding a coordinator to a school', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator.selected_school_id}`)

        // filling the form
        cy.get('#add_coordinator_first_name').type(coordinator.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator.last_name)
        cy.get('#add_coordinator_email').type(coordinator.email)
        cy.get('#add_coordinator_phone_number').type(coordinator.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school
        cy.contains(coordinator.first_name).parents(`#school_${coordinator.selected_school_id}_row`)
    })

    it('Adding a coordinator to a school with a missing field', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_last_name').type(coordinator.last_name)
        cy.get('#add_coordinator_email').type(coordinator.email)
        cy.get('#add_coordinator_phone_number').type(coordinator.phone_number)

        cy.get('add_coordinator_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#add_coordinator_alert').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid email address', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_first_name').type(coordinator.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator.last_name)
        cy.get('#add_coordinator_email').type("iloveanigmailcom")
        cy.get('#add_coordinator_phone_number').type(coordinator.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error snackbar alert should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid phone number', () => {
        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button_${coordinator.selected_school_id}`)

        // filling the form but missing the first name
        cy.get('#add_coordinator_first_name').type(coordinator.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator.last_name)
        cy.get('#add_coordinator_email').type(coordinator.email)
        cy.get('#add_coordinator_phone_number').type("ab")

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error snackbar alert should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.contains(coordinator.first_name).should('not.exist')
    })

    it('Removing a coordinator from a school', () => {
        // opening the removing dialog
        cy.get(`#remove_coordinator_${coordinator.email}`).click()

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
})