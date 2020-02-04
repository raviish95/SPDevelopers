package com.awizom.spdeveloper.Model;

public class TeamLeaderModel {

    public int AdminTeamLeaderID ;
    public int EmployeeID ;
    public String CreatedOn ;

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String EmployeeName ;
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

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

}
