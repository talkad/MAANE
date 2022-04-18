
describe('Login test', () =>{
    beforeEach(() => {
        // visiting the login page
        cy.visit('/user/login')
    })

    it('Logging in with an existing user', () => {

        // filling the form
        cy.get('input[id=login_username]').type('tal').should('have.value','tal')
        cy.get('input[id=login_password]').type('1234').should('have.value','1234')

        // submitting
        cy.get('[id=login_button]').click()

        // not expecting the error alert to be visible
        cy.get('[id=login_alert]').should('not.be.visible')

        // checking the login was successful by checking the url
        cy.url().should('include', '/user/home')
    })

    it('Logging in with a user which does not exist', () => {

        // filling the form
        cy.get('input[id=login_username]').type('yosi').should('have.value','yosi')
        cy.get('input[id=login_password]').type('1111').should('have.value','1111')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/home')
    })

    it('Logging in with an existing user but with incorrect password', () => {

        // filling the form
        cy.get('input[id=login_username]').type('tal').should('have.value','tal')
        cy.get('input[id=login_password]').type('123').should('have.value','123')

        // submitting
        cy.get('[id=login_button]').click()

        // should expect an alert error to appear
        cy.get('[id=login_alert]').should('be.visible')

        // checking we remained in the same url
        cy.url().should('include', '/user/home')
    })

    it('logs in programmatically without using the UI', () => {
        cy.request({
            method: 'POST',
            url: "http://localhost:8080/user/login",
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: {
                username: "tal",
                password: "1234",
            }
        }).then((response) => {
            console.log(response)
            window.sessionStorage.setItem('access_token', response.body.access_token);
            window.sessionStorage.setItem('refresh_token', response.body.refresh_token);
        })
    })
})