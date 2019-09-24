package com.fixed.fixitservices.Services;

public class ServiceModel {
    private String id, name, description;
    boolean active, deleted;
    int serviceBasePrice, peakPrice;
    int commercialServicePrice, commercialServicePeakPrice;

    String imageUrl;
    boolean offeringCommercialService, offeringResidentialService;

    int position;

    public ServiceModel() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isOfferingCommercialService() {
        return offeringCommercialService;
    }

    public void setOfferingCommercialService(boolean offeringCommercialService) {
        this.offeringCommercialService = offeringCommercialService;
    }

    public boolean isOfferingResidentialService() {
        return offeringResidentialService;
    }

    public void setOfferingResidentialService(boolean offeringResidentialService) {
        this.offeringResidentialService = offeringResidentialService;
    }

    public int getCommercialServicePrice() {
        return commercialServicePrice;
    }

    public void setCommercialServicePrice(int commercialServicePrice) {
        this.commercialServicePrice = commercialServicePrice;
    }

    public int getCommercialServicePeakPrice() {
        return commercialServicePeakPrice;
    }

    public void setCommercialServicePeakPrice(int commercialServicePeakPrice) {
        this.commercialServicePeakPrice = commercialServicePeakPrice;
    }

    public int getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(int peakPrice) {
        this.peakPrice = peakPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getServiceBasePrice() {
        return serviceBasePrice;
    }

    public void setServiceBasePrice(int serviceBasePrice) {
        this.serviceBasePrice = serviceBasePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
