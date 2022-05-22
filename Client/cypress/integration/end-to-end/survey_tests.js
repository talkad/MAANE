
let survey_answers = {
    surveyID: "1111",
    schoolID: "1111111",
    OPEN_ANSWER: "hello there",
    NUMERIC_ANSWER: "66",
    MULTIPLE_CHOICE: "0",
}

describe('Survey tests', () => {
    beforeEach(() => {
        cy.request({
            method: 'POST',
            url: "http://localhost:8080/data/resetDB",
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(() => {

            cy.visit(`/survey/getSurvey?surveyID=${survey_answers.surveyID}`)
        })
    })

    it('Successfully submitting a survey', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('#submit_survey_submit_button').click() // submitting

        // a success alert should pop up
        cy.get('#submit_survey_success_alert').should('be.visible')
    })

    it('Submitting a survey but leaving the symbol question empty', () => {
        // the first question is always the school's symbol and we're leaving it empty

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Submitting a survey but leaving a blank open answer', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check()

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Submitting a survey but leaving a blank numeric answer', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Submitting a survey but not checking a multiple choice question', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Trying to fill a numeric answer with general text', () => {

        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type(survey_answers.schoolID)

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_numeric_answer_${2}`).type("Anakin don't try it")
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

    it('Going to a different path from the survey page', () => {
        // Since all the users who answer the survey are guests
        // And this is a close system. They should have access only to this page
        cy.wait(1000)

        // visiting the home page
        cy.visit('/user/home')

        // the text of the 404 page
        cy.contains('דף זה אינו קיים')
    })

    it('Submitting a non existent school symbol', () => {
        // the first question is always the school's symbol
        cy.get(`#submit_survey_numeric_answer_${0}`).type('1000')

        // it was decided that the first will be open, the second will be numeric open and the third will be multiple
        cy.get(`#submit_survey_open_answer_${1}`).type(survey_answers.OPEN_ANSWER)
        cy.get(`#submit_survey_numeric_answer_${2}`).type(survey_answers.NUMERIC_ANSWER)
        cy.get(`#submit_survey_multi_choice_${3}_${survey_answers.MULTIPLE_CHOICE}`).check(survey_answers.MULTIPLE_CHOICE)

        cy.get('#submit_survey_submit_button').click() // submitting

        // an error alert should pop up
        cy.get('#snackbar_alert_error').should('be.visible')
    })

})