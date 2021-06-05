package com.bahaadev.pushnotification;


public class UserModel {
    private final String firstName,secondName,email,password,reg_token;



    public UserModel(String firstName, String secondName, String email, String password, String reg_token) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.password = password;
        this.reg_token = reg_token;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getReg_token() {
        return reg_token;
    }
}
