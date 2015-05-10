package com.nexters.nochat;

public class AddressBook {
    private  String phonenum;
    private  String name;

    public AddressBook(){}
    public AddressBook(String name, String phonenum) {
        this.name = name;
        this.phonenum = phonenum;
    }

    public String getPhonenum() {
        return phonenum;
    }
    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
