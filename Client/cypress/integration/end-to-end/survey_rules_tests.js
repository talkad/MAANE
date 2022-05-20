
let supervisor = {
    username: "ronit",
    password: "1234abcd",
}

let preloaded_survey_id = 2222;

let selected_goal = '1'

let selected_multiple_question = '3'

let selected_numeric_question = '2'
let selected_numeric_inequality = 'LESS_THAN'
let selected_numeric_number = '42'

let cell_indexer = 1

let no_rules_exist_string = "לא קיימים חוקים";

describe('Survey rules tests', () => {
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

            cy.visit(`/survey/rules?surveyID=${preloaded_survey_id}`)

            cy.url().should('include', `/survey/rules?surveyID=${preloaded_survey_id}`)

            cell_indexer = 1
        })
    })

    afterEach(() => {
        // logging out cause it clashes with the other tests
        cy.get('[id=logout_button]').click()

        cy.url().should('include', '/user/login')
    })

    it("Successfully adding a basic rule for a multiple choice question", () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // making sure the rules were saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('not.be.visible')
    })

    it("Successfully adding a basic rule for a numeric question", () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // making sure the rules were saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('not.be.visible')
    })

    it("Successfully adding a compound OR rule", () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the OR option
        cy.get(`#question-select-label-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="OR"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // making sure the rules were saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('not.be.visible')
    })

    it("Successfully adding a compound AND rule", () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the AND option
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')

        // making sure the rules were saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('not.be.visible')
    })

    it('Changing a compound cell into a basic question', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the AND option
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // changing the AND to a question
        cy.get(`#question-select-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // the cells with the id of 2 and 3 should not exist
        cy.get(`#question-select-label-${3}`).should('not.exist')
        cy.get(`#question-select-label-${4}`).should('not.exist')
    })

    it('Removing a compound cell', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the AND option
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // changing the AND to a question
        cy.get(`#remove_cell_${2}`).click()

        // the cells with the id of 1 and its children 2 and 3 should not exist
        cy.get(`#question-select-label-${2}`).should('not.exist')
        cy.get(`#question-select-label-${3}`).should('not.exist')
        cy.get(`#question-select-label-${4}`).should('not.exist')
    })

    // it('Leaving a multiple choice question cell without ticked checkboxes', () => {
    //     cy.wait(500)
    //
    //     // adding cell
    //     cy.get('#survey_rules_add_rule_button').click()
    //
    //     // choosing goal
    //     cy.get(`#goal-select-${cell_indexer}`)
    //         .parent()
    //         .click()
    //         .get(`ul > li[data-value="${selected_goal}"]`)
    //         .click()
    //
    //     // adding an inner cell
    //     cy.get(`#add-inner-cell-${cell_indexer}`).click()
    //
    //     cell_indexer++
    //
    //     // selecting the multiple choice question
    //     cy.get(`#question-select-${cell_indexer}`)
    //         .parent()
    //         .click()
    //         .get(`ul > li[data-value="${selected_multiple_question}"]`)
    //         .click()
    //
    //     // submitting rules
    //     cy.get('#survey_rules_submit_rules_button').click()
    //
    //     // an error notification snackbar should appear
    //     cy.get('#snackbar_alert_error').should('be.visible')
    // })

    it('Leaving a numeric question cell without picking an inequality', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${cell_indexer}`).type(selected_numeric_number)

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // making sure the rules were not saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('be.visible')
    })

    it('Leaving a numeric question cell without inputting a number', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the numeric question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`)
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // making sure the rules were not saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('be.visible')
    })

    it('Not picking a goal for a rule', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        cell_indexer++

        // selecting the multiple choice question
        cy.get(`#question-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${cell_indexer}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // making sure the rules were not saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('be.visible')
    })

    it('Not picking a question or compound operation in an inner cell in a rule', () => {
        cy.wait(500)

        // adding cell
        cy.get('#survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${cell_indexer}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`)
            .click()

        // adding an inner cell
        cy.get(`#add-inner-cell-${cell_indexer}`).click()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')

        // making sure the rules were not saved
        cy.reload()
        cy.wait(500)
        cy.get('#no-rules-message').should('be.visible')
    })
})