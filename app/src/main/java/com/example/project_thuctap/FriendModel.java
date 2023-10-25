//FriendModel

package com.example.project_thuctap;


public class FriendModel {
    private String email;
    private String name;
    private String phone;
    private int reply;
    public FriendModel(String email, String name, String phone, int reply) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.reply = reply;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }


}
