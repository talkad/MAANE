
let selected_user_as_supervisor = {
    username: "vivenna",
}

describe('User management test as a supervisor', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a supervisor
        cy.visit('/user/home')

        cy.get(`#user_collapse_button_${selected_user_as_supervisor.username}`).click()
    })

    it('Successfully change password to user', () => {
        // opening the change password dialog
        cy.get(`#change_password_${selected_user_as_supervisor.username}`).click()

    })

})

describe('User management test as a system manager', () => {
    beforeEach(() => {
        //TODO: format the db and log in as a system manager
        cy.visit('/user/home')
    })
})