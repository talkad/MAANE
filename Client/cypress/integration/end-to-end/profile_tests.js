

describe('Profile page tests for editing info', () => {
    beforeEach(() => {
        //TODO: format the db and log in
        cy.visit('/user/profile')

        // pressing the edit button
        cy.get('#profile_edit_button').click()
    })

    it('', () => {


    })
})

describe('Profile page tests for security features', () => {
    it('', () => {

    })
})