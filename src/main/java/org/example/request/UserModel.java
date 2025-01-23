package org.example.request;

import lombok.Getter;
import lombok.Setter;

public class UserModel {
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String name;

    public UserModel() {}

    public UserModel(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}