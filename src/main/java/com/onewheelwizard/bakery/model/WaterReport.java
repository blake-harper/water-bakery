package com.onewheelwizard.bakery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onewheelwizard.bakery.model.constants.WaterCondition;
import com.onewheelwizard.bakery.model.constants.WaterType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.ZonedDateTime;

@Entity
public class WaterReport {
    //Member variables
    @JsonIgnore
    @ManyToOne
    Account account;

    @Id
    @GeneratedValue
    private Long id;

    private ZonedDateTime postDate;

    private double latitude;
    private double longitude;

    @Enumerated(EnumType.STRING)
    private WaterType waterType;

    @Enumerated(EnumType.STRING)
    private WaterCondition waterCondition;

    //Constructors
    WaterReport() { // For JPA!
    }

    public WaterReport(Account account, ZonedDateTime postDate, double latitude, double longitude,
            WaterType waterType, WaterCondition waterCondition) {
        this.account = account;
        this.postDate = postDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waterType = waterType;
        this.waterCondition = waterCondition;
    }

    //Getters and setters
    public Account getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getPostDate() {
        return postDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public WaterType getWaterType() {
        return waterType;
    }

    public WaterCondition getWaterCondition() {
        return waterCondition;
    }

    @JsonProperty("authorUsername")
    public String getAuthorUsername() {
        if (account!= null) {
            return account.getUsername();
        } else {
            return null;
        }
    }
}
