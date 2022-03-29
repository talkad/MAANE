package Domain.DataManagement;

/*
 * Represents School that gets his information from exel
 */
public class School {
    String symbol;
    String manager;
    String managerPhone;
    String coordinator;
    String coordinatorPhone;
    String coordinatorEmail;
    String city;
    // status?

    public School (String symbol, String manager, String managerPhone, String coordinator, String coordinatorPhone,String coordinatorEmail, String city){
        this.symbol = symbol;
        this.manager = manager;
        this.managerPhone = managerPhone;
        this.coordinator = coordinator;
        this.coordinatorPhone = coordinatorPhone;
        this.coordinatorEmail = coordinatorEmail;
        this.city = city;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public String getCoordinatorPhone() {
        return coordinatorPhone;
    }

    public void setCoordinatorPhone(String coordinatorPhone) {
        this.coordinatorPhone = coordinatorPhone;
    }

    public String getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public void setCoordinatorEmail(String coordinatorEmail) {
        this.coordinatorEmail = coordinatorEmail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
