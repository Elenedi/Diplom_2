package org.example.request;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String name;

    public User() {}

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}