package org.example.http.client;

import io.restassured.response.Response;
import org.example.Constants;
import org.example.http.client.Basis;


public class User extends Basis {
    public Response registerUser (User user){
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
    public Response loginUser (User user){
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
}