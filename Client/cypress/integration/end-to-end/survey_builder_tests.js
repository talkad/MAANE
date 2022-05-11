
let survey_data = {
    title: "hello there",
    description: "general kenobi",

    questions: [{index: 0, question_title: "anakin don't try it", type: 'OPEN_ANSWER'},
        {index: 1, question_title: "you underestimate my power", type: 'NUMERIC_ANSWER'},
        {index: 2, question_title: "luke, im your father", type: 'MULTIPLE_CHOICE', answers: ["no", "NO", "NOOO"]}]
}

describe('Survey builder tests', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor

        cy.visit('/survey/createSurvey')
    })

    it('Successfully creating a survey', () => {
        // title and description of the survey
        cy.get('#create_survey_title').type(survey_data.title);
        cy.get('#create_survey_description').type(survey_data.description);

        for (const question of survey_data.questions){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            cy.get(`#create_question_title_${question.index}`).type(question.question_title)

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()

            // in the case of multiple, adding answers
            if(question.type === 'MULTIPLE_CHOICE'){

                let answer_index = 0
                for(const answer of question.answers){
                    cy.get(`#create_question_multiple_add_answer_button_${question.index}`).click()
                    cy.get(`#question-${question.index}-answer-${answer_index}`).type(answer)
                    answer_index++;
                }
            }

        }

        // submitting the survey
        cy.get('#create_survey_submit_survey_button').click()

        // a success alert should appear
        cy.get('#create_survey_success_alert').should('be.visible')

        // going back to the menu
        cy.get('#create_survey_go_back_button').click()

        // should find the title of the new survey in the menu
        cy.find(survey_data.title)
    })

    it('Creating a survey with an empty title', () => {
        // title and description of the survey
        cy.get('#create_survey_description').type(survey_data.description);

        for (const question of survey_data.questions){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            cy.get(`#create_question_title_${question.index}`).type(question.question_title)

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()

            // in the case of multiple, adding answers
            if(question.type === 'MULTIPLE_CHOICE'){

                let answer_index = 0
                for(const answer of question.answers){
                    cy.get(`#create_question_multiple_add_answer_button_${question.index}`).click()
                    cy.get(`#question-${question.index}-answer-${answer_index}`).type(answer)
                    answer_index++;
                }
            }

        }

        // submitting the survey
        cy.get('#create_survey_submit_survey_button').click()

        // an error alert should appear
        cy.get('#create_survey_error_alert')

        // going back to the menu
        cy.visit('/survey/menu')

        // should not find the title of the new survey in the menu
        cy.contains(survey_data.title).should('not.exist')
    })

    it('Creating a survey with an empty description', () => {
        // title and description of the survey
        cy.get('#create_survey_title').type(survey_data.title);

        for (const question of survey_data.questions){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            cy.get(`#create_question_title_${question.index}`).type(question.question_title)

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()

            // in the case of multiple, adding answers
            if(question.type === 'MULTIPLE_CHOICE'){

                let answer_index = 0
                for(const answer of question.answers){
                    cy.get(`#create_question_multiple_add_answer_button_${question.index}`).click()
                    cy.get(`#question-${question.index}-answer-${answer_index}`).type(answer)
                    answer_index++;
                }
            }

        }

        // submitting the survey
        cy.get('#create_survey_submit_survey_button').click()

        // an error alert should appear
        cy.get('#create_survey_error_alert')

        // going back to the menu
        cy.visit('/survey/menu')

        // should not find the title of the new survey in the menu
        cy.contains(survey_data.title).should('not.exist')
    })

    it('Creating a survey with an empty question title', () => {
        // title and description of the survey
        cy.get('#create_survey_title').type(survey_data.title);
        cy.get('#create_survey_description').type(survey_data.description);

        for (const question of survey_data.questions){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            if(question.index !== 0){
                cy.get(`#create_question_title_${question.index}`).type(question.question_title)
            }

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()

            // in the case of multiple, adding answers
            if(question.type === 'MULTIPLE_CHOICE'){

                let answer_index = 0
                for(const answer of question.answers){
                    cy.get(`#create_question_multiple_add_answer_button_${question.index}`).click()
                    cy.get(`#question-${question.index}-answer-${answer_index}`).type(answer)
                    answer_index++;
                }
            }

        }

        // submitting the survey
        cy.get('#create_survey_submit_survey_button').click()

        // an error alert should appear
        cy.get('#create_survey_error_alert')

        // going back to the menu
        cy.visit('/survey/menu')

        // should not find the title of the new survey in the menu
        cy.contains(survey_data.title).should('not.exist')
    })

    it('Creating a survey with an empty answer title', () => {
        // title and description of the survey
        cy.get('#create_survey_title').type(survey_data.title);
        cy.get('#create_survey_description').type(survey_data.description);

        for (const question of survey_data.questions){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            cy.get(`#create_question_title_${question.index}`).type(question.question_title)

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()

            // in the case of multiple, adding answers
            if(question.type === 'MULTIPLE_CHOICE'){

                let answer_index = 0
                for(const answer of question.answers){
                    cy.get(`#create_question_multiple_add_answer_button_${question.index}`).click()

                    if(answer_index !== 0){
                        cy.get(`#question-${question.index}-answer-${answer_index}`).type(answer)
                    }
                    answer_index++;
                }
            }
        }

        // submitting the survey
        cy.get('#create_survey_submit_survey_button').click()

        // an error alert should appear
        cy.get('#create_survey_error_alert')

        // going back to the menu
        cy.visit('/survey/menu')

        // should not find the title of the new survey in the menu
        cy.contains(survey_data.title).should('not.exist')
    })

    it('Deletion of question', () => {
        // adding 2 questions

        for (const question of survey_data.questions.slice(0, 2)){
            // adding question
            cy.get('#create_survey_add_question_button').click()

            cy.get(`#create_question_title_${question.index}`).type(question.question_title)

            cy.get(`#create_question_type_selection_${question.index}`) // selecting type from the menu
                .parent()
                .click()
                .get(`ul > li[data-value="${question.type}"]`)
                .click()
        }

        // removing the first question
        cy.get(`#create_question_delete_button_${survey_data.questions[0].index}`).click()

        // shouldn't find the first question but should the second
        cy.contains(survey_data.questions[0].question_title).should('not.exist')
        cy.contains(survey_data.questions[1].question_title).should('exist')
    })

    it('Deletion of an answer to a question', () => {
        // adding a question
        cy.get('#create_survey_add_question_button').click()

        // adding two answers
        cy.get(`#create_question_multiple_add_answer_button_${0}`).click()
        cy.get(`#question-${0}-answer-${0}`).type('goodanswer1')

        cy.get(`#create_question_multiple_add_answer_button_${0}`).click()
        cy.get(`#question-${0}-answer-${1}`).type('greatanswer2')

        // removing the first answer
        cy.get(`#create_question_multiple_answer_remove_button_${0}_${0}`).click()

        // shouldn't find the first answer but should the second
        cy.contains('goodanswer1').should('not.exist')
        cy.contains('greatanswer2').should('exist')
    })
})