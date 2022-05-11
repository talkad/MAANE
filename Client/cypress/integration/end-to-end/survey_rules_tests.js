
let preloaded_survey_id = 123

let selected_goal = 'idk'

let selected_multiple_question = 'idk2'

let selected_numeric_question = 'idk3'
let selected_numeric_inequality = 'LESS_THAN'
let selected_numeric_number = '42'

describe('Survey rules tests', () => {
    beforeEach(() => {
        // todo: get the id of preloaded survey
        cy.visit(`/survey/rules?surveyID=${preloaded_survey_id}`)
    })

    it("Successfully adding a basic rule for a multiple choice question", () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${1}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')
    })

    it("Successfully adding a basic rule for a numeric question", () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${1}`).type(selected_numeric_number)

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')
    })

    it("Successfully adding a compound OR rule", () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the OR option
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="OR"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`add-inner-cell-${1}`)
        cy.get(`add-inner-cell-${1}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${2}`).type(selected_numeric_number)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${3}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${3}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')
    })

    it("Successfully adding a compound AND rule", () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the AND option
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`add-inner-cell-${1}`)
        cy.get(`add-inner-cell-${1}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${2}`).type(selected_numeric_number)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${3}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${3}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // a success notification snackbar should appear
        cy.get('#snackbar_alert_success').should('be.visible')
    })

    it('Changing a compound cell into a basic question', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the AND option
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`add-inner-cell-${1}`)
        cy.get(`add-inner-cell-${1}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${2}`).type(selected_numeric_number)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${3}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${3}-${0}`).check()

        // changing the AND to a question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`)
            .click()

        // the cells with the id of 2 and 3 should not exist
        cy.get(`#question-select-label-${2}`).should('not.exist')
        cy.get(`#question-select-label-${3}`).should('not.exist')
    })

    it('Removing a compound cell', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the AND option
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="AND"]`)
            .click()

        // adding an inner 2 cells
        cy.get(`add-inner-cell-${1}`)
        cy.get(`add-inner-cell-${1}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${2}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${2}`).type(selected_numeric_number)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${3}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${3}-${0}`).check()

        // changing the AND to a question
        cy.get(`#remove_cell_${1}`).click()

        // the cells with the id of 1 and its children 2 and 3 should not exist
        cy.get(`#question-select-label-${1}`).should('not.exist')
        cy.get(`#question-select-label-${2}`).should('not.exist')
        cy.get(`#question-select-label-${3}`).should('not.exist')
    })

    it('Leaving a multiple choice question cell without ticked checkboxes', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Leaving a numeric question cell without picking an inequality', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // inputting a number
        cy.get(`#inequality-number-input-${1}`).type(selected_numeric_number)

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Leaving a numeric question cell without inputting a number', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the numeric question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // selecting inequality
        cy.get(`#inequality-select-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_numeric_inequality}"]`)
            .click()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Not picking a goal for a rule', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // selecting the multiple choice question
        cy.get(`#question-select-label-${1}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_multiple_question}"]`) // TODO: select the multiple choice question once there is a mock db
            .click()

        // ticking the first answer box
        cy.get(`#constraint-checkbox-${1}-${0}`).check()

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Not picking a question or compound operation in an inner cell in a rule', () => {
        // adding cell
        cy.get('survey_rules_add_rule_button').click()

        // choosing goal
        cy.get(`#goal-select-${0}`)
            .parent()
            .click()
            .get(`ul > li[data-value="${selected_goal}"]`) // TODO: select a goal once there is a mock db
            .click()

        // adding an inner cell
        cy.get(`add-inner-cell-${0}`)

        // submitting rules
        cy.get('#survey_rules_submit_rules_button').click()

        // an error notification snackbar should appear
        cy.get('#snackbar_alert_error').should('be.visible')
    })
})