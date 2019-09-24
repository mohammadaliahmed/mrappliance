package com.fixed.fixitservices.Models;

import java.util.ArrayList;

public class OrderModel {
    long orderId;
    long time;
    User user;
    ArrayList<ServiceCountModel> countModelArrayList;
    ArrayList<ServiceCountModel> newCountModelList;

    long totalPrice;
    String instructions;
    String date, chosenTime;
    String orderStatus;
    String orderAddress;
    double lat, lon;
    long billNumber;
    String buildingType;
    long totalHours;
    String serviceName,serviceId;
    boolean modifiedOrderConfirmed, assigned;
    String assignedTo;

    String assignedToName;
    boolean arrived;
    long materialBill;
    long jobEndTime;
    boolean jobDone;
    boolean rated;
    float rating;
    boolean couponApplied;
    String couponCode;
    int discount;
    boolean cancelled;
    String cancelReason;
    boolean commercialBuilding;
    boolean jobStarted;
    String googleAddress;
    boolean jobFinish;
    long jobStartTime;
    boolean peakHour;
    long serviceCharges;
    int tax;


    public OrderModel(long orderId, long time, User user, ArrayList<ServiceCountModel> countModelArrayList,
                      long totalPrice, long totalHours, String instructions, String date, String chosenTime,
                      String orderStatus, String orderAddress,
                      double lat, double lon,
                      String buildingType, String serviceName,String serviceId, boolean couponApplied,
                      String couponCode,
                      int discount, boolean commercialBuilding,int tax) {
        this.orderId = orderId;
        this.time = time;
        this.user = user;
        this.countModelArrayList = countModelArrayList;
        this.totalPrice = totalPrice;
        this.instructions = instructions;
        this.date = date;
        this.chosenTime = chosenTime;
        this.orderStatus = orderStatus;
        this.orderAddress = orderAddress;
        this.serviceId = serviceId;
        this.lat = lat;
        this.lon = lon;
        this.buildingType = buildingType;
        this.totalHours = totalHours;
        this.serviceName = serviceName;
        this.couponApplied = couponApplied;
        this.couponCode = couponCode;
        this.discount = discount;
        this.commercialBuilding = commercialBuilding;
        this.tax = tax;

    }


    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public boolean isJobFinish() {
        return jobFinish;
    }

    public void setJobFinish(boolean jobFinish) {
        this.jobFinish = jobFinish;
    }

    public long getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(long jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public boolean isPeakHour() {
        return peakHour;
    }

    public void setPeakHour(boolean peakHour) {
        this.peakHour = peakHour;
    }

    public long getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(long serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public boolean isJobStarted() {
        return jobStarted;
    }

    public void setJobStarted(boolean jobStarted) {
        this.jobStarted = jobStarted;
    }

    public boolean isCommercialBuilding() {
        return commercialBuilding;
    }

    public void setCommercialBuilding(boolean commercialBuilding) {
        this.commercialBuilding = commercialBuilding;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public boolean isCouponApplied() {
        return couponApplied;
    }

    public void setCouponApplied(boolean couponApplied) {
        this.couponApplied = couponApplied;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public OrderModel() {
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public long getMaterialBill() {
        return materialBill;
    }

    public void setMaterialBill(long materialBill) {
        this.materialBill = materialBill;
    }

    public long getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(long jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public boolean isJobDone() {
        return jobDone;
    }

    public void setJobDone(boolean jobDone) {
        this.jobDone = jobDone;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public ArrayList<ServiceCountModel> getNewCountModelList() {
        return newCountModelList;
    }

    public void setNewCountModelList(ArrayList<ServiceCountModel> newCountModelList) {
        this.newCountModelList = newCountModelList;
    }

    public boolean isModifiedOrderConfirmed() {
        return modifiedOrderConfirmed;
    }

    public void setModifiedOrderConfirmed(boolean modifiedOrderConfirmed) {
        this.modifiedOrderConfirmed = modifiedOrderConfirmed;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(long totalHours) {
        this.totalHours = totalHours;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<ServiceCountModel> getCountModelArrayList() {
        return countModelArrayList;
    }

    public void setCountModelArrayList(ArrayList<ServiceCountModel> countModelArrayList) {
        this.countModelArrayList = countModelArrayList;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChosenTime() {
        return chosenTime;
    }

    public void setChosenTime(String chosenTime) {
        this.chosenTime = chosenTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public long getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(long billNumber) {
        this.billNumber = billNumber;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }
}
