
let survey_answers = {
    surveyID: "idk",
    schoolID: "123",
    OPEN_ANSWER: "hello there",
    NUMERIC_ANSWER: "66",
    MULTIPLE_CHOICE: "0",
}

describe('Survey tests', () => {
    beforeEach(() => {
        // TODO: get the id of the preloaded survey from the db
        cy.visit(`survey/schoolID=${survey_answers.surveyID}`)
    })

    it('Successfully submitting a survey', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`submit_survey_multi_choice_${3}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('submit_survey_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('submit_survey_success_alert').should('be.visible')
    })

    it('Submitting a survey but leaving a blank open answer', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`submit_survey_multi_choice_${3}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('submit_survey_submit_button').click() // submitting

        // an error alerts should pop up
        cy.get('submit_survey_error_alert').should('be.visible')
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Submitting a survey but leaving a blank numeric answer', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`submit_survey_multi_choice_${3}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('submit_survey_submit_button').click() // submitting

        // an error alerts should pop up
        cy.get('submit_survey_error_alert').should('be.visible')
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Submitting a survey but not checking a multiple choice question', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)

        cy.get('submit_survey_submit_button').click() // submitting

        // an error alerts should pop up
        cy.get('submit_survey_error_alert').should('be.visible')
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Trying to fill a numeric answer with general text', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`submit_survey_numeric_answer_${2}`).type("Anakin don't try it")
        cy.get(`submit_survey_multi_choice_${3}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('submit_survey_submit_button').click() // submitting

        // an error alerts should pop up
        cy.get('submit_survey_error_alert').should('be.visible')
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Going to a different path from the survey page', () => {
        // Since all the users who answer the survey are guests
        // And this is a close system. They should have access only to this page

        // visiting the home page
        cy.visit('/user/home')

        // the text of the 404 page
        cy.find('דף זה אינו קיים')
    })

    it('Submitting a non existent school symbol', () => {
        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type('0000')

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`submit_survey_multi_choice_${3}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('submit_survey_submit_button').click() // submitting

        // an error alerts should pop up
        cy.get('submit_survey_error_alert').should('be.visible')
        cy.get('#snackbar_alert_error').should('be.visible')
    })

})