package com.nexters.nochat;

public class UsrIdVO {
    private String usr_phoneNumber;
    private String usr_Id;
    private Object usr_Ids;

    public UsrIdVO(){}
    public UsrIdVO(String usr_phoneNumber, String usr_Id){
        super();
        this.usr_phoneNumber = usr_phoneNumber;
        this.usr_Id = usr_Id;
    }
    public UsrIdVO(String usr_Id){
        super();
        this.usr_Id = usr_Id;
    }
    public UsrIdVO(Object usr_Ids){
        super();
        this.usr_Ids = usr_Ids;
    }

    public String getUsr_phoneNumber() {
        return usr_phoneNumber;
    }

    public void setUsr_phoneNumber(String usr_phoneNumber) {
        this.usr_phoneNumber = usr_phoneNumber;
    }

    public String getUsr_Id() {
        return usr_Id;
    }

    public void setUsr_Id(String usr_Id) {
        this.usr_Id = usr_Id;
    }
}
