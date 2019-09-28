package com.awizom.spdeveloper.Model;

public class FollowUpModel {
    public int FollowUpID ;
    public int ClientID;
    public int EmployeeID ;
    public  String Name;
    public String FollowUpMethod ;
    public String FollowUpDate;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getFollowUpID() {
        return FollowUpID;
    }

    public void setFollowUpID(int followUpID) {
        FollowUpID = followUpID;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getFollowUpMethod() {
        return FollowUpMethod;
    }

    public void setFollowUpMethod(String followUpMethod) {
        FollowUpMethod = followUpMethod;
    }

    public String getFollowUpDate() {
        return FollowUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
        FollowUpDate = followUpDate;
    }


}
