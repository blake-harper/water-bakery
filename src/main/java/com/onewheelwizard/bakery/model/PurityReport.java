package com.onewheelwizard.bakery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private float virusPPM;
    private float contaminantPPM;

    //Constructors

    PurityReport() { // For JPA!
    }

    public PurityReport(Account account, ZonedDateTime postDate, double latitude, double longitude,WaterPurityCondition waterPurityCondition, float virusPPM,
                        float contaminantPPM) {
        this.account = account;
        this.postDate = postDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waterPurityCondition = waterPurityCondition;
        this.virusPPM = virusPPM;
        this.contaminantPPM = contaminantPPM;
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

    public float getVirusPPM() {
        return virusPPM;
    }

    public float getContaminantPPM() {
        return contaminantPPM;
    }

    public WaterPurityCondition getWaterPurityCondition() {
        return waterPurityCondition;
    }

    @JsonProperty("authorUsername")
    public String getAuthorUsername() {
        return account.getUsername();
    }
}
