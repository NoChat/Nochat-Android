package com.nexters.nochat;

public class UserInfoVO {
    private String userId;
    private String userName;

    public UserInfoVO(String userName){
        super();
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
