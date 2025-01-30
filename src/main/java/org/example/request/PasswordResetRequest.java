package org.example.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String email;

    private String password;

    private String token;

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    public PasswordResetRequest(String password, String token) {
        this.password = password;
        this.token = token;
    }
}

