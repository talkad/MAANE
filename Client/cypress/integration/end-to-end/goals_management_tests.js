
let supervisor = {
    username: 'ronit',
    password: '1234abcd',
}

let currentYear = new Date().getFullYear();

let goal_to_add = {
    id: 0,
    title: "the tragedy of darth plagueis the wise",
    describe: "did you ever hear the tragedy of darth palgueis the wise? i thoguht not. it's not a story the jedi would tell you, it's a sith legend",
    weight: 2,
    quarter: 3,
    year: currentYear
}

let goal_to_remove = {
    id: 1,
    title: "yahad1",
    year: currentYear,
}

describe('Goals management tests', () => {
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
            cy.visit('/user/goalsManagement')
            cy.get('#goal_management_add_goal_collapse_button').click()
        })



    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it("adding a goal", () => {
        // filling the form
        cy.get('#add_goal_title').type(goal_to_add.title)
        cy.get('#add_goal_description').type(goal_to_add.describe)

        cy.get('#add_goal_weigh_selection') // selecting weight
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.weight}]`)
            .click()

        cy.get('#add_goal_quarter_selection') // selecting quarter
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.quarter}]`)
            .click()

        cy.get('#add_goal_hebrew_year_picker') // selecting year
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.get('#add_goal_submit_button').click() // submitting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // checking if the goal is added to the table under the year it was set
        cy.contains(goal_to_add.title).should('not.be.visible') // shouldn't be visible right away cause it was not set under the default year (the current one)

        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.contains(goal_to_add.title).should('be.visible') // should be visible
    })

    it('adding a goal with a missing field', () => {
        // filling the form but missing the title
        cy.get('#add_goal_description').type(goal_to_add.describe)

        cy.get('#add_goal_weigh_selection') // selecting weight
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.weight}]`)
            .click()

        cy.get('#add_goal_quarter_selection') // selecting quarter
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.quarter}]`)
            .click()

        cy.get('#add_goal_hebrew_year_picker') // selecting year
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.get('#add_goal_submit_button').click() // submitting

        // an error should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // checking the goal wasn't added to the table under the year it was meant to be
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.contains(goal_to_add.title).should('not.exist') // should not exist
    })

    it('deleting an existing goal', () => {

        // deleting the goal
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_remove.year}]`)
            .click()


        cy.get(`#delete_goal_${goal_to_remove.id}`).click()

        // making sure the dialog popped up
        cy.get('#delete_goal_dialog').should('be.visible')

        cy.get('#delete_goal_submit').click() // deleting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // the goal should not appear anymore
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_remove.year}]`)
            .click()

        cy.contains(goal_to_remove.title).should('not.exist')
    })

    it('going to delete a goal but pressing cancel', () => {

        // deleting the goal
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_remove.year}]`)
            .click()

        cy.get(`#delete_goal_${goal_to_remove.id}`).click()

        // making sure the dialog popped up
        cy.get('#delete_goal_dialog').should('be.visible')

        cy.get('#delete_goal_cancel').click() // canceling

        // checking if the goal still exists
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_remove.year}]`)
            .click()

        cy.contains(goal_to_remove.title).should('exist') // should be visible
    })
})