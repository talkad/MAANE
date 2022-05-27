
let supervisor = {
    username: 'ronit',
    password: '1234abcd'
}

let coordinator_to_add = {
    selected_school_id: '2222222',
    first_name: "padme",
    last_name: "amidala",
    email: "iloveani@gmail.com",
    phone_number: "052-6735471"
}

let coordinator_to_remove = {
    selected_school_id: '1111111',
    name: 'aviad shal',
}

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

    it('Adding a coordinator to a school', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button`).click()

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
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()


        // checking if we can find the coordinator in the table under the selected school
        cy.contains(coordinator_to_add.first_name)
    })

    it('Adding a coordinator to a school with a missing field', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button`).click()

        // filling the form but missing the first name
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type(coordinator_to_add.email)
        cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#add_coordinator_alert').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.get('#add_coordinator_cancel_button').click() // cancel

        cy.reload()

        // search up the school again
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid email address', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button`).click()

        // filling the form but missing the first name
        cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type("iloveanigmailcom")
        cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error snackbar alert should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking if we can find the coordinator in the table under the selected school (should not)
        cy.reload()

        // search up the school again
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Adding a coordinator to a school with an invalid phone number', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // popping up the add coordinator dialog
        cy.get(`#school_add_coordinator_button`).click()

        // filling the form but missing the first name

        cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
        cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
        cy.get('#add_coordinator_email').type(coordinator_to_add.email)
        cy.get('#add_coordinator_phone_number').type('050')

        cy.get('#add_coordinator_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')

        cy.reload()

        // search up the school again
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        cy.contains(coordinator_to_add.first_name).should('not.exist')
    })

    it('Removing a coordinator from a school', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_remove.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // opening the removing dialog
        cy.get(`#remove_coordinator`).click()

        cy.get('#remove_coordinator_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        cy.reload()

        // search up the school again
        cy.get('#search-schools').type(coordinator_to_remove.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        // checking the coordinator was removed
        cy.contains(coordinator_to_remove.name).should('not.exist')
    })

    it('Removing a coordinator from a school while someone else already removed the same coordinator', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_remove.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        cy.wait(1000)

        // removing coordinator programmatically
        cy.request({
            method: 'POST',
            url: "http://localhost:8080/data/removeCoordinatorTester",
            body: {
                "school": `${coordinator_to_remove.selected_school_id}`
            }
        }).then(() => {

            // opening the removing dialog
            cy.get(`#remove_coordinator`).click()

            cy.get('#remove_coordinator_submit_button').click() // submitting

            // an error snackbar should appear
            cy.get('#snackbar_alert_error').should('be.visible')

            cy.reload()

            // search up the school again
            cy.get('#search-schools').type(coordinator_to_remove.selected_school_id + "{downArrow}{enter}")
            cy.get('#search_school_button').click()

            // checking the coordinator was removed
            cy.contains(coordinator_to_remove.name).should('not.exist')
        })
    })

    it('Adding a coordinator to a school while someone else already added a different coordinator', () => {
        // searching up the school
        cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
        cy.get('#search_school_button').click()

        cy.wait(1000)

        // adding coordinator programmatically
        cy.request({
            method: 'POST',
            url: "http://localhost:8080/data/assignCoordinatorTester",
            body: {
                "school": `${coordinator_to_add.selected_school_id}`
            }
        }).then(() => {

            // popping up the add coordinator dialog
            cy.get(`#school_add_coordinator_button`).click()

            // filling the form
            cy.get('#add_coordinator_first_name').type(coordinator_to_add.first_name)
            cy.get('#add_coordinator_last_name').type(coordinator_to_add.last_name)
            cy.get('#add_coordinator_email').type(coordinator_to_add.email)
            cy.get('#add_coordinator_phone_number').type(coordinator_to_add.phone_number)

            cy.get('#add_coordinator_submit_button').click() // submitting

            // an error snackbar should appear
            cy.get('#snackbar_alert_error').should('be.visible')

            cy.reload()

            // search up the school again
            cy.get('#search-schools').type(coordinator_to_add.selected_school_id + "{downArrow}{enter}")
            cy.get('#search_school_button').click()

            // checking if we can find the coordinator in the table under the selected school (should not)
            cy.contains(coordinator_to_add.first_name).should('not.exist')
        })
    })
})