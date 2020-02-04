package com.awizom.spdeveloper.Model;

public class NotificationModel {

    public int ClientLeadNotiId ;
    public int AdminTeamLeaderID ;
    public int EmployeeID ;
    public String MSGBody ;
    public Boolean isRead ;

    public int getClientLeadNotiId() {
        return ClientLeadNotiId;
    }

    public void setClientLeadNotiId(int clientLeadNotiId) {
        ClientLeadNotiId = clientLeadNotiId;
    }

    public int getAdminTeamLeaderID() {
        return AdminTeamLeaderID;
    }

    public void setAdminTeamLeaderID(int adminTeamLeaderID) {
        AdminTeamLeaderID = adminTeamLeaderID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getMSGBody() {
        return MSGBody;
    }

    public void setMSGBody(String MSGBody) {
        this.MSGBody = MSGBody;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String CreatedOn;

}
