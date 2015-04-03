package com.nexters.nochat;

public class UsrFriendsVO {

    private String usr_phoneNumber;
    private String usr_Name;

    public UsrFriendsVO(String usr_phoneNumber, String usr_Name){
        super();
        this.usr_phoneNumber = usr_phoneNumber;
        this.usr_Name = usr_Name;
    }
    public UsrFriendsVO(String usr_Name){
        super();
        this.usr_Name = usr_Name;
    }

    public String getUsr_phoneNumber() {
        return usr_phoneNumber;
    }

    public void setUsr_phoneNumber(String usr_phoneNumber) {
        this.usr_phoneNumber = usr_phoneNumber;
    }

    public String getUsr_Name() {
        return usr_Name;
    }

    public void setUsr_Name(String usr_Name) {
        this.usr_Name = usr_Name;
    }

}
