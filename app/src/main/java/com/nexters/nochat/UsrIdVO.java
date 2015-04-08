package com.nexters.nochat;

public class UsrIdVO {
    private String mUsr_phoneNumber;
    private String mUsr_Id;

    public UsrIdVO(String usr_phoneNumber, String usr_Id){
        super();
        this.mUsr_phoneNumber = usr_phoneNumber;
        this.mUsr_Id = usr_Id;
    }
    public UsrIdVO(String usr_Id){
        super();
        this.mUsr_Id = usr_Id;
    }

    public String getmUsr_Id() {
        return mUsr_Id;
    }

    public void setmUsr_Id(String mUsr_Id) {
        this.mUsr_Id = mUsr_Id;
    }
}
