package com.awizom.spdeveloper.Model;

public class ClientPropertyModel {
    public int CPropertyID ;
    public int ClientID ;
    public String PropertyName ;
    public String PropertyArea;
    public String Photo ;
    public String Address;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String CreatedBy ;

    public int getCPropertyID() {
        return CPropertyID;
    }

    public void setCPropertyID(int CPropertyID) {
        this.CPropertyID = CPropertyID;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public String getPropertyName() {
        return PropertyName;
    }

    public void setPropertyName(String propertyName) {
        PropertyName = propertyName;
    }

    public String getPropertyArea() {
        return PropertyArea;
    }

    public void setPropertyArea(String propertyArea) {
        PropertyArea = propertyArea;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }


    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String CreatedOn ;
    }
