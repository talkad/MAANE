import gematriya from "gematriya";

let currentYear = new Date().getFullYear();

let goal_to_add = {
    id: 0, // TODO: figure the id its going to get
    title: "the tragedy of darth plagueis the wise",
    describe: "did you ever hear the tragedy of darth palgueis the wise? i thoguht not. it's not a story the jedi would tell you, it's a sith legend",
    weight: 2,
    quarter: 3,
    year: gematriya(currentYear + 3760 + 1, {punctuate: true, limit: 3}),
}

describe('Goals management tests', () => {
    beforeEach(() => {
        //TODO: format the db and log in a supervisor
        cy.visit('/user/goalsManagement')
        cy.get('#goal_management_add_goal_collapse_button').click()
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
        cy.get('#add_goal_alert').should('be.visible')

        // checking the goal wasn't added to the table under the year it was meant to be
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.contains(goal_to_add.title).should('not.exist') // should not exist
    })

    it('deleting an existing goal', () => {
        // adding a goal
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

        // deleting the goal
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.get(`#delete_goal_${goal_to_add.id}`).click()

        // making sure the dialog popped up
        cy.get('#delete_goal_dialog').should('be.visible')

        cy.get('#delete_goal_submit').click() // deleting

        // a success snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // the goal should not appear anymore
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)

        cy.contains(goal_to_add.title).should('not.exist')
    })

    it('going to delete a goal but pressing cancel', () => {
        // adding a goal
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

        // deleting the goal
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.get(`#delete_goal_${goal_to_add.id}`).click()

        // making sure the dialog popped up
        cy.get('#delete_goal_dialog').should('be.visible')

        cy.get('#delete_goal_cancel').click() // canceling

        // checking if the goal still exists
        cy.get('#goals_management_year_selection') // selecting year in the table
            .parent()
            .click()
            .get(`ul > li[data-value=${goal_to_add.year}]`)
            .click()

        cy.contains(goal_to_add.title).should('be.visible') // should be visible
    })
})