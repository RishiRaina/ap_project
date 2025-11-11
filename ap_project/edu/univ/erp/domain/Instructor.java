package edu.univ.erp.domain;

public class Instructor {
    private int id;
    private String name;
    private String email;
    private String specialization;

    public Instructor(int id, String name, String email, String specialization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
    }

    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSpecialization() { return specialization; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
