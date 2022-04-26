package Persistence.DbDtos;

public class SchoolDBDTO {
    String symbol;
    String name;
    String city;
    String city_mail;
    String address;
    String school_address;
    String principal;
    String manager;
    String supervisor;
    String phone;
    String mail;
    int zipcode;
    String education_stage;
    String education_type;
    String supervisor_type;
    String spector;
    int num_of_students;

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCity_mail() {
        return city_mail;
    }

    public String getAddress() {
        return address;
    }

    public String getSchool_address() {
        return school_address;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getManager() {
        return manager;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public int getZipcode() {
        return zipcode;
    }

    public String getEducation_stage() {
        return education_stage;
    }

    public String getEducation_type() {
        return education_type;
    }

    public String getSupervisor_type() {
        return supervisor_type;
    }

    public String getSpector() {
        return spector;
    }

    public int getNum_of_students() {
        return num_of_students;
    }

    public SchoolDBDTO(String symbol, String name, String city, String city_mail, String address, String school_address, String principal, String manager, String supervisor, String phone, String mail, int zipcode, String education_stage, String education_type, String supervisor_type, String spector, int num_of_students) {
        this.symbol = symbol;
        this.name = name;
        this.city = city;
        this.city_mail = city_mail;
        this.address = address;
        this.school_address = school_address;
        this.principal = principal;
        this.manager = manager;
        this.supervisor = supervisor;
        this.phone = phone;
        this.mail = mail;
        this.zipcode = zipcode;
        this.education_stage = education_stage;
        this.education_type = education_type;
        this.supervisor_type = supervisor_type;
        this.spector = spector;
        this.num_of_students = num_of_students;
    }
}
