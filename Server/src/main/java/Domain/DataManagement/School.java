package Domain.DataManagement;

import Communication.DTOs.UserDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Represents School that gets his information from exel
 */
public class School {
    String symbol;
    String manager;
    String managerPhone;
    Map<String, UserDTO> coordinators;
    String city;
    // status?

    public School (String symbol, String manager, String managerPhone, String city){
        this.symbol = symbol;
        this.manager = manager;
        this.managerPhone = managerPhone;
        coordinators = new ConcurrentHashMap<>();
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, UserDTO> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(Map<String, UserDTO> coordinators) {
        this.coordinators = coordinators;
    }
}
