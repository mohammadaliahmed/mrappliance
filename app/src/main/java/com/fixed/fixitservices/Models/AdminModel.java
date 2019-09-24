package com.fixed.fixitservices.Models;

public class AdminModel {
    String adminNumber,fcmKey,providingServiceInCities;
    int tax;

    public AdminModel() {
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getProvidingServiceInCities() {
        return providingServiceInCities;
    }

    public void setProvidingServiceInCities(String providingServiceInCities) {
        this.providingServiceInCities = providingServiceInCities;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public String getAdminNumber() {
        return adminNumber;
    }

    public void setAdminNumber(String adminNumber) {
        this.adminNumber = adminNumber;
    }
}
