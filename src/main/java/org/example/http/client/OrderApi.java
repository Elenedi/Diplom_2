package org.example.http.client;

import io.restassured.response.Response;
import org.example.*;

public class OrderApi extends Basis {

    public Response createOrder(OrderApi orderApi, String token) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.ORDERS,
                orderApi,
                "application/json",
                token);
    }

    public Response createOrder(OrderApi orderApi) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.ORDERS,
                orderApi,
                "application/json"
        );
    }
    public Response getComponentsList() {
        return doGetRequest(
                Constants.SERVER_NAME + Constants.COMPONENTS);

    }
    public Response getOrderList(String token) {
        return doGetRequest(
                Constants.SERVER_NAME + Constants.ORDERS,
                token);
    }

    public Response getAllOrderList() {
        return doGetRequest(
                Constants.SERVER_NAME + Constants.ALL_ORDERS);
    }
}