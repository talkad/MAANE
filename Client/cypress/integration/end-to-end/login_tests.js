
// TODO: fill the form with real values when the server works again
describe('Login test', () =>{
    it('Logging in with an existing user', () => {
        cy.visit('http://localhost:3000/user/login')

        // filling the form
        cy.get('input[id=login_username]').type('stam').should('have.value','stam')
        cy.get('input[id=login_password]').type('clum').should('have.value','clum')

        // submitting
        cy.get('login_button').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')
    })

    it('Logging in with a user which does not exist', () => {
        cy.visit('http://localhost:3000/user/login')

        // filling the form
        cy.get('input[id=login_username]').type('stam').should('have.value','stam')
        cy.get('input[id=login_password]').type('clum').should('have.value','clum')

        // submitting
        cy.get('login_button').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

    })

    it('Logging in with an existing user but with incorrect password', () => {
        cy.visit('http://localhost:3000/user/login')

        // filling the form
        cy.get('input[id=login_username]').type('stam').should('have.value','stam')
        cy.get('input[id=login_password]').type('clum').should('have.value','clum')

        // submitting
        cy.get('login_button').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')
    })
})