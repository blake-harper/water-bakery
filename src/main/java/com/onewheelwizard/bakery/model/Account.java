package com.onewheelwizard.bakery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onewheelwizard.bakery.model.constants.UserType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {
    // Member variables
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<WaterReport> waterReports = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<PurityReport> purityReports = new HashSet<>();


    private String username;
    @JsonIgnore
    private String password;
    private UserType userType;
    private String email;
    private String title;
    private String city;

    // Constructors
    Account() { // For JPA!
    }

    public Account(String username, String password, UserType userType, String email, String title, String city) {
        this.username = username;
        this.password = password; //TODO salt and hash instead of storing raw; probably some spring-y way to do it
        this.userType = userType;
        this.email = email;
        this.title = title;
        this.city = city;
    }


    // Getters/Setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public Set<PurityReport> getPurityReports() {
        return purityReports;
    }

    public Set<WaterReport> getWaterReports() {
        return waterReports;
    }

    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public void setPassword(String password) {
        //todo salt/hash - spring might have a handler for this
        this.password = password;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
