package org.example.http.client;

import io.restassured.response.Response;
import org.example.Constants;
import org.example.request.PasswordResetRequest;

public class User extends Basis {
    public Response registerUser (String user, String password, String name){
        return doPostRequest(
                Constants.SERVER_NAME + Constants.REGISTER_USER,
                user,
                "application/json");
    }

    public Response deleteUser (String token){
        return doDeleteRequest(
                Constants.SERVER_NAME + Constants.USER,
                token);
    }
    public Response loginUser (String user, String password){
        return doPostRequest(
                Constants.SERVER_NAME + Constants.LOGIN_USER,
                user,
                "application/json"
        );
    }
    public Response updateUser (User user, String token){
        return doPatchRequest(
                Constants.SERVER_NAME + Constants.USER,
                user,
                "application/json",
                token
        );
    }
    public Response updateUser (User user){
        return doPatchRequest(
                Constants.SERVER_NAME + Constants.USER,
                user,
                "application/json"
        );
    }

    public Response requestPasswordReset(String email) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.RESET_PASSWORD,
                new PasswordResetRequest(email),
                "application/json");

    }

    public Response resetPassword(String newPassword, String token) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.RESET_PASSWORD + "/reset",
                new PasswordResetRequest(newPassword, token),
                "apllication/json");
    }
}