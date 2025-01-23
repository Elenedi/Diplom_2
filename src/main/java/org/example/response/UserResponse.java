package org.example.response;

import lombok.Getter;
import lombok.Setter;
import org.example.request.UserModel;

public class UserResponse {
    @Getter
    @Setter
    private String success;

    @Getter
    @Setter
    private UserModel user;

    @Getter
    @Setter
    private String accessToken;

    @Getter
    @Setter
    private String refreshToken;

    @Getter
    @Setter
    private String message;

    public UserResponse() { }

    public UserResponse(String success, UserModel user, String accessToken, String refreshToken) {
        this.success = success;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserResponse(String success, String message) {
        this.success = success;
        this.message = message;
    }
}

