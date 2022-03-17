package Communication.DTOs;

import java.util.List;

public class SchoolManagementDTO {
    private String currUser;
    private String affectedUser;
    private List<String> schools;

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getAffectedUser() {
        return affectedUser;
    }

    public void setAffectedUser(String affectedUser) {
        this.affectedUser = affectedUser;
    }

    public List<String> getSchools() {
        return schools;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }
}
