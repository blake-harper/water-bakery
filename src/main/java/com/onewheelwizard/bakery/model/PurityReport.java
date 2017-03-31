package com.onewheelwizard.bakery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class PurityReport {
    //Member variables
    @JsonIgnore
    @ManyToOne
    private Account account;

    @Id
    @GeneratedValue
    private Long id;

    private ZonedDateTime postDate;

    private double latitude;
    private double longitude;

    @Enumerated(EnumType.STRING)
    private WaterPurityCondition waterPurityCondition;
    private float virusPpm;
    private float contaminantPpm;

    //Constructors

    PurityReport() { // For JPA!
    }

    public PurityReport(Account account, ZonedDateTime postDate, double latitude, double longitude,WaterPurityCondition waterPurityCondition, float virusPpm,
                        float contaminantPpm) {
        this.account = account;
        this.postDate = postDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waterPurityCondition = waterPurityCondition;
        this.virusPpm = virusPpm;
        this.contaminantPpm = contaminantPpm;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public ZonedDateTime getPostDate() {
        return postDate;
    }

    public Account getAccount() {
        return account;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getVirusPpm() {
        return virusPpm;
    }

    public float getContaminantPpm() {
        return contaminantPpm;
    }

    public WaterPurityCondition getWaterPurityCondition() {
        return waterPurityCondition;
    }

    @JsonProperty("authorUsername")
    public String getAuthorUsername() {
        return account.getUsername();
    }
}
